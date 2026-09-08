package ls.co.bytewise.api.user;

import ls.co.bytewise.api.common.Status;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.security.TenantContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private static final String PASSWORD_CHARS =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%";

    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public UserService(UserRepository userRepository,
                       SchoolRepository schoolRepository,
                       PasswordEncoder passwordEncoder,
                       JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.schoolRepository = schoolRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Transactional
    public UserDto createUser(User creator, UserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DataIntegrityViolationException("A user with this email already exists");
        }

        if (dto.getRoles() != null && dto.getRoles().contains(UserRole.SYSTEM_ADMIN)) {
            throw new IllegalArgumentException("System Administrator accounts cannot be created this way");
        }

        Long schoolId = TenantContext.getSchoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalStateException("Active school not found"));

        String temporaryPassword = generateTemporaryPassword();

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setMustChangePassword(true);
        user.setStatus(Status.ACTIVE);
        user.setRoles(dto.getRoles() != null ? dto.getRoles() : Set.of());
        user.setSchools(Set.of(school));
        user.setCreatedBy(creator);
        user.setUpdatedBy(creator);

        User saved = userRepository.save(user);

        sendWelcomeEmail(saved.getEmail(), saved.getFirstName(), temporaryPassword);

        return new UserDto(saved);
    }

    public List<UserDto> listUsers() {
        Long schoolId = TenantContext.getSchoolId();
        return userRepository.findAllBySchoolId(schoolId).stream()
                .map(UserDto::new)
                .toList();
    }

    @Transactional
    public UserDto updateUser(User editor, Long userId, UserDto dto) {
        User user = getUserInCurrentSchool(userId);

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setUpdatedBy(editor);

        if (dto.getRoles() != null && !dto.getRoles().contains(UserRole.SYSTEM_ADMIN)) {
            user.setRoles(dto.getRoles());
        }

        User saved = userRepository.save(user);
        return new UserDto(saved);
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = getUserInCurrentSchool(userId);
        user.setStatus(Status.INACTIVE);
        userRepository.save(user);
    }

    private User getUserInCurrentSchool(Long userId) {
        Long schoolId = TenantContext.getSchoolId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean belongsToSchool = user.getSchools().stream()
                .anyMatch(school -> school.getId().equals(schoolId));

        if (!belongsToSchool) {
            throw new IllegalArgumentException("User not found");
        }

        return user;
    }

    private String generateTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            password.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }

        return password.toString();
    }

    private void sendWelcomeEmail(String toEmail, String firstName, String temporaryPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your School Management System account");
        message.setText(
                "Hi " + firstName + ",\n\n" +
                        "An account has been created for you on the School Management System.\n\n" +
                        "Email: " + toEmail + "\n" +
                        "Temporary password: " + temporaryPassword + "\n\n" +
                        "Please log in and change your password immediately.\n\n" +
                        "http://localhost:4200/login"
        );

        mailSender.send(message);
    }
}