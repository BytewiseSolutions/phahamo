package ls.co.bytewise.api.user;

import ls.co.bytewise.api.common.Status;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.parent.ParentDto;
import ls.co.bytewise.api.parent.ParentService;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.StudentDto;
import ls.co.bytewise.api.student.StudentService;
import ls.co.bytewise.api.teacher.TeacherDto;
import ls.co.bytewise.api.teacher.TeacherService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
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
    private final StudentService studentService;
    private final ParentService parentService;
    private final TeacherService teacherService;

    public UserService(UserRepository userRepository,
                       SchoolRepository schoolRepository,
                       PasswordEncoder passwordEncoder,
                       JavaMailSender mailSender,
                       StudentService studentService,
                       ParentService parentService,
                       TeacherService teacherService
                       ) {
        this.userRepository = userRepository;
        this.schoolRepository = schoolRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.studentService = studentService;
        this.parentService = parentService;
        this.teacherService = teacherService;
    }

    @Transactional
    public UserDto createUser(User creator, UserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DataIntegrityViolationException("A user with this email already exists");
        }

        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            throw new UserExceptions.InvalidRoleAssignmentException("At least one role is required");
        }

        if (dto.getRoles().contains(UserRole.SYSTEM_ADMIN)) {
            throw new UserExceptions.InvalidRoleAssignmentException(
                    "System Administrator accounts cannot be created this way");
        }

        boolean creatorIsSystemAdmin = creator.hasRole(UserRole.SYSTEM_ADMIN);

        School school = null;
        if (!creatorIsSystemAdmin) {
            Long schoolId = TenantContext.getSchoolId();
            school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new IllegalStateException("Active school not found"));
        }

        String temporaryPassword = generateTemporaryPassword();

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setMustChangePassword(true);
        user.setStatus(Status.ACTIVE);
        user.setRoles(dto.getRoles());
        user.setSchools(school != null ? Set.of(school) : Set.of());
        user.setCreatedBy(creator);
        user.setUpdatedBy(creator);

        User saved = userRepository.save(user);

        if (dto.getRoles().contains(UserRole.STUDENT)) {
            if (dto.getStudent() == null) {
                throw new UserExceptions.InvalidRoleAssignmentException(
                        "Student details are required when assigning the STUDENT role");
            }
            studentService.createProfileFor(creator, saved, dto.getStudent());
        }

        if (dto.getRoles().contains(UserRole.PARENT)) {
            if (dto.getParent() == null) {
                throw new UserExceptions.InvalidRoleAssignmentException(
                        "Parent details are required when assigning the PARENT role");
            }
            parentService.createProfileFor(creator, saved, dto.getParent());
        }

        if (dto.getRoles().contains(UserRole.TEACHER)) {
            if (dto.getTeacher() == null) {
                throw new UserExceptions.InvalidRoleAssignmentException(
                        "Teacher details are required when assigning the TEACHER role");
            }
            teacherService.createProfileFor(creator, saved, dto.getTeacher());
        }

        sendWelcomeEmail(saved.getEmail(), saved.getFirstName(), temporaryPassword);

        return new UserDto(saved);
    }

    public List<UserDto> listUsers() {
        Long schoolId = TenantContext.getSchoolId();
        return userRepository.findAllBySchoolId(schoolId).stream()
                .map(UserDto::new)
                .toList();
    }

    public List<UserDto> listTeachers() {
        Long schoolId = TenantContext.getSchoolId();

        return userRepository.findAllBySchoolIdAndRole(schoolId, UserRole.TEACHER).stream()
                .map(UserDto::new)
                .toList();
    }

    public List<UserDto> listParents() {
        Long schoolId = TenantContext.getSchoolId();

        return userRepository.findAllBySchoolIdAndRole(schoolId, UserRole.PARENT).stream()
                .map(UserDto::new)
                .toList();
    }

    @Transactional
    public UserDto updateUser(User editor, Long userId, UserDto dto) {
        User user = getUserInCurrentSchool(userId);

        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }

        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }

        user.setUpdatedBy(editor);

        if (dto.getRoles() != null) {
            if (dto.getRoles().isEmpty()) {
                throw new UserExceptions.InvalidRoleAssignmentException("At least one role is required");
            }
            if (dto.getRoles().contains(UserRole.SYSTEM_ADMIN)) {
                throw new UserExceptions.InvalidRoleAssignmentException(
                        "System Administrator accounts cannot be created this way");
            }
            user.setRoles(dto.getRoles());
        }

        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
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

    @Transactional
    public void activateUser(Long userId) {
        User user = getUserInCurrentSchool(userId);
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
    }

    private User getUserInCurrentSchool(Long userId) {
        Long schoolId = TenantContext.getSchoolId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserExceptions.UserNotFoundException("User not found"));

        boolean belongsToSchool = user.getSchools().stream()
                .anyMatch(school -> school.getId().equals(schoolId));

        if (!belongsToSchool) {
            throw new UserExceptions.UserNotFoundException("User not found");
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

    @Async
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

    public UserDto getUser(Long userId) {
        User user = getUserInCurrentSchool(userId);
        UserDto dto = new UserDto(user);

        if (user.hasRole(UserRole.STUDENT)) {
            dto.setStudent(studentService.getByUserId(userId));
        }

        if (user.hasRole(UserRole.PARENT)) {
            dto.setParent(parentService.getByUserId(userId));
        }

        if (user.hasRole(UserRole.TEACHER)) {
            dto.setTeacher(teacherService.getByUserId(userId));
        }

        return dto;
    }
}