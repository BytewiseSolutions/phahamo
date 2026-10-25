package ls.co.bytewise.api.user.auth;

import ls.co.bytewise.api.audit.AuditAction;
import ls.co.bytewise.api.audit.AuditLogService;
import ls.co.bytewise.api.common.Status;
import ls.co.bytewise.api.security.JwtService;
import ls.co.bytewise.api.security.PasswordResetToken;
import ls.co.bytewise.api.security.PasswordResetTokenRepository;
import ls.co.bytewise.api.security.TokenBlacklistService;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.user.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private static final int ACCOUNT_FAILURE_LIMIT = 5;
    private static final int IP_FAILURE_LIMIT = 10;
    private static final long LOCK_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JavaMailSender mailSender;
    private final AuditLogService auditLogService;
    private final ConcurrentMap<String, IpAttemptWindow> ipAttempts = new ConcurrentHashMap<>();

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 TokenBlacklistService tokenBlacklistService,
                                 PasswordResetTokenRepository passwordResetTokenRepository,
                                 JavaMailSender mailSender,
                                 AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.mailSender = mailSender;
        this.auditLogService = auditLogService;
    }

    public LoginResponse login(LoginRequest request) {
        return login(request, "unknown");
    }

    @Transactional
    public LoginResponse login(LoginRequest request, String clientIp) {
        String source = clientIp == null || clientIp.isBlank() ? "unknown" : clientIp;
        if (isIpBlocked(source)) {
            throw new BadCredentialsException("Too many failed attempts from this network. Try again in 15 minutes.");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    registerIpFailure(source);
                    return new BadCredentialsException("Invalid email or password");
                });

        Instant now = Instant.now();
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(now)) {
            long minutes = Math.max(1, ChronoUnit.MINUTES.between(now, user.getLockedUntil()) + 1);
            throw new BadCredentialsException("This account is temporarily locked. Try again in " + minutes + " minutes.");
        }

        if (user.getLockedUntil() != null && !user.getLockedUntil().isAfter(now)) {
            user.setLockedUntil(null);
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
        }

        if (user.getStatus() != Status.ACTIVE) {
            throw new BadCredentialsException("Account is " + user.getStatus().name().toLowerCase());
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            boolean accountLocked = recordFailedLogin(user, source);
            if (accountLocked) {
                throw new BadCredentialsException("Your account has been temporarily locked for 15 minutes after 5 failed attempts.");
            }
            if (isIpBlocked(source)) {
                throw new BadCredentialsException("Too many failed attempts from this network. Try again in 15 minutes.");
            }
            throw new BadCredentialsException("Invalid email or password");
        }

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
        ipAttempts.remove(source);
        return issueLoginResponse(user);
    }

    public LoginResponse issueLoginResponse(User user) {
        String token = jwtService.generateToken(user);

        user.getSchools().stream().findFirst().ifPresent(school ->
                auditLogService.record(school, user, AuditAction.LOGIN,
                        "USER", user.getId(), "Logged in successfully"));

        String schoolName = user.getSchools().stream()
                .findFirst()
                .map(school -> school.getName())
                .orElse("");

        return new LoginResponse(
                token,
                user.getId(),
                user.getFullName(),
                user.getRoles(),
                user.getSchools().stream().map(school -> school.getId()).collect(Collectors.toSet()),
                schoolName,
                user.isMustChangePassword()
        );
    }

    public void logout(String token, User user) {
        Date expiration = jwtService.extractExpiration(token);
        tokenBlacklistService.blacklist(token, expiration.toInstant());
        user.getSchools().stream().findFirst().ifPresent(school ->
                auditLogService.record(school, user, AuditAction.LOGOUT,
                        "USER", user.getId(), "Logged out"));
    }

    private boolean recordFailedLogin(User user, String clientIp) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        boolean accountLocked = user.getFailedLoginAttempts() >= ACCOUNT_FAILURE_LIMIT;
        if (accountLocked) {
            user.setLockedUntil(Instant.now().plus(LOCK_MINUTES, ChronoUnit.MINUTES));
        }
        userRepository.save(user);

        registerIpFailure(clientIp);
        String reason = accountLocked ? "account locked after 5 failed attempts" : "invalid password";
        user.getSchools().stream().findFirst().ifPresent(school ->
                auditLogService.record(school, user, AuditAction.FAILED_LOGIN,
                        "USER", user.getId(), "Failed login attempt: " + reason));

        if (accountLocked) {
            sendLockoutEmail(user);
        }

        return accountLocked;
    }

    private boolean isIpBlocked(String clientIp) {
        IpAttemptWindow window = ipAttempts.get(clientIp);
        if (window == null) return false;
        if (window.blockedUntil != null && window.blockedUntil.isAfter(Instant.now())) return true;
        if (window.windowStartedAt.plus(LOCK_MINUTES, ChronoUnit.MINUTES).isBefore(Instant.now())) {
            ipAttempts.remove(clientIp);
        }
        return false;
    }

    private boolean registerIpFailure(String clientIp) {
        Instant now = Instant.now();
        IpAttemptWindow window = ipAttempts.compute(clientIp, (key, existing) -> {
            if (existing == null || existing.windowStartedAt.plus(LOCK_MINUTES, ChronoUnit.MINUTES).isBefore(now)) {
                return new IpAttemptWindow(1, now, null);
            }
            existing.failures++;
            if (existing.failures >= IP_FAILURE_LIMIT) {
                existing.blockedUntil = now.plus(LOCK_MINUTES, ChronoUnit.MINUTES);
            }
            return existing;
        });
        return window.blockedUntil != null && window.blockedUntil.isAfter(now);
    }

    private void sendLockoutEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) return;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Your school account has been temporarily locked");
        message.setText("Hi " + user.getFirstName() + ",\n\n"
                + "Your School Management System account was temporarily locked after 5 failed login attempts.\n"
                + "You can try again in 15 minutes, or contact your school administrator to unlock it sooner.\n\n"
                + "If this was not you, please reset your password.\n\n"
                + "School Management System");
        try {
            mailSender.send(message);
        } catch (Exception ignored) {
            // Login protection must still work when the notification provider is unavailable.
        }
    }

    private static final class IpAttemptWindow {
        private int failures;
        private final Instant windowStartedAt;
        private Instant blockedUntil;

        private IpAttemptWindow(int failures, Instant windowStartedAt, Instant blockedUntil) {
            this.failures = failures;
            this.windowStartedAt = windowStartedAt;
            this.blockedUntil = blockedUntil;
        }
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return;
        }

        String rawToken = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(rawToken);
        resetToken.setUser(user);
        resetToken.setExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));
        resetToken.setUsed(false);

        passwordResetTokenRepository.save(resetToken);

        sendResetEmail(user.getEmail(), rawToken);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired reset token"));

        if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException("Invalid or expired reset token");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    private void sendResetEmail(String toEmail, String rawToken) {
        String resetLink = "http://localhost:4200/reset-password?token=" + rawToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset your School Management System password");
        message.setText("Click the link below to reset your password. This link expires in 1 hour.\n\n" + resetLink);

        mailSender.send(message);
    }

    @Transactional
    public void changePassword(User currentUser, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        currentUser.setMustChangePassword(false);
        userRepository.save(currentUser);
    }
}
