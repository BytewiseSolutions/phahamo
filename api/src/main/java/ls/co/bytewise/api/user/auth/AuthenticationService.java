package ls.co.bytewise.api.user.auth;

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
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JavaMailSender mailSender;

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 TokenBlacklistService tokenBlacklistService,
                                 PasswordResetTokenRepository passwordResetTokenRepository,
                                 JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.mailSender = mailSender;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (user.getStatus() != Status.ACTIVE) {
            throw new BadCredentialsException("Account is " + user.getStatus().name().toLowerCase());
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

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

    public void logout(String token) {
        Date expiration = jwtService.extractExpiration(token);
        tokenBlacklistService.blacklist(token, expiration.toInstant());
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