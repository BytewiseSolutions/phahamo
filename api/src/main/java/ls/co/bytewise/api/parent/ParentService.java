package ls.co.bytewise.api.parent;

import ls.co.bytewise.api.common.Status;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.mail.MailService;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentExceptions;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.user.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ParentService {

    private static final String PASSWORD_CHARS =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%";

    private static final int MAX_SEARCH_RESULTS = 10;

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final String frontendUrl;

    public ParentService(ParentRepository parentRepository,
                         StudentRepository studentRepository,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         MailService mailService,
                         @Value("${app.frontend-url:http://localhost:4200}") String frontendUrl) {
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.frontendUrl = frontendUrl.replaceAll("/$", "");
    }

    @Transactional
    public ParentDto createProfileFor(User creator, User user, ParentDto dto) {
        Long schoolId = TenantContext.getSchoolId();

        Parent parent = new Parent();
        parent.setUser(user);
        parent.setSchool(user.getSchools().stream()
                .filter(s -> s.getId().equals(schoolId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User is not attached to active school")));
        parent.setRelationship(dto.getRelationship());
        parent.setLoginEnabled(true);
        parent.setInvitedAt(Instant.now());
        parent.setCreatedBy(creator);
        parent.setUpdatedBy(creator);

        Parent saved = parentRepository.save(parent);
        return new ParentDto(saved);
    }

    @Transactional
    public StudentParentDto createAndLinkParent(User creator, Long studentId, ParentDto dto) {
        Long schoolId = TenantContext.getSchoolId();

        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new StudentExceptions.StudentNotFoundException("Student not found"));

        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            if (!user.hasRole(UserRole.PARENT)) {
                throw new DataIntegrityViolationException(
                        "A non-parent user already uses this email");
            }

            Parent existingParent = parentRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Parent profile missing for existing user"));

            if (studentRepository.existsParentLink(studentId, existingParent.getId())) {
                throw new ParentExceptions.DuplicateLinkException(
                        "This parent is already linked to this student");
            }

            student.getParents().add(existingParent);
            student.setUpdatedBy(creator);
            studentRepository.save(student);

            return new StudentParentDto(existingParent);
        }

        String temporaryPassword = generateTemporaryPassword();

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setMustChangePassword(true);
        user.setStatus(Status.INACTIVE);
        user.setRoles(Set.of(UserRole.PARENT));
        user.setSchools(Set.of(student.getSchool()));
        user.setCreatedBy(creator);
        user.setUpdatedBy(creator);

        User savedUser = userRepository.save(user);

        Parent parent = new Parent();
        parent.setUser(savedUser);
        parent.setSchool(student.getSchool());
        parent.setRelationship(dto.getRelationship());
        parent.setLoginEnabled(false);
        parent.setCreatedBy(creator);
        parent.setUpdatedBy(creator);

        Parent savedParent = parentRepository.save(parent);

        student.getParents().add(savedParent);
        student.setUpdatedBy(creator);
        studentRepository.save(student);

        return new StudentParentDto(savedParent);
    }

    @Transactional
    public StudentParentDto inviteParent(User inviter, Long parentId) {
        Parent parent = findOrThrow(parentId);
        User user = parent.getUser();
        String temporaryPassword = generateTemporaryPassword();

        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setMustChangePassword(true);
        user.setStatus(Status.ACTIVE);
        user.setUpdatedBy(inviter);
        parent.setLoginEnabled(true);
        parent.setInvitedAt(Instant.now());
        parent.setUpdatedBy(inviter);
        userRepository.save(user);
        Parent saved = parentRepository.save(parent);

        sendWelcomeEmail(user.getEmail(), user.getFirstName(), temporaryPassword,
                parent.getSchool().getName(), parent.getSchool());
        return new StudentParentDto(saved);
    }

    @Transactional
    public void disableParentLogin(User editor, Long parentId) {
        Parent parent = findOrThrow(parentId);
        parent.setLoginEnabled(false);
        parent.setUpdatedBy(editor);
        parentRepository.save(parent);

        User user = parent.getUser();
        user.setStatus(Status.INACTIVE);
        user.setUpdatedBy(editor);
        userRepository.save(user);
    }

    @Transactional
    public StudentParentDto linkExistingParent(User creator, Long studentId, Long parentId) {
        Long schoolId = TenantContext.getSchoolId();
        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new StudentExceptions.StudentNotFoundException("Student not found"));

        Parent parent = findOrThrow(parentId);

        if (studentRepository.existsParentLink(studentId, parentId)) {
            throw new ParentExceptions.DuplicateLinkException("This parent is already linked to this student");
        }

        student.getParents().add(parent);
        student.setUpdatedBy(creator);
        studentRepository.save(student);

        return new StudentParentDto(parent);
    }

    public List<StudentParentDto> listParentsForStudent(Long studentId) {
        Long schoolId = TenantContext.getSchoolId();
        if (!studentRepository.existsByIdAndSchoolId(studentId, schoolId)) {
            throw new StudentExceptions.StudentNotFoundException("Student not found");
        }
        return studentRepository.findParentsByStudentId(studentId).stream()
                .map(StudentParentDto::new)
                .toList();
    }

    @Transactional
    public ParentDto updateParent(User editor, Long parentId, ParentDto dto) {
        Parent parent = findOrThrow(parentId);

        parent.setRelationship(dto.getRelationship());
        parent.setUpdatedBy(editor);

        Parent saved = parentRepository.save(parent);
        return new ParentDto(saved);
    }

    @Transactional
    public void unlinkParent(Long studentId, Long parentId) {
        Long schoolId = TenantContext.getSchoolId();
        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new StudentExceptions.StudentNotFoundException("Student not found"));

        if (!studentRepository.existsParentLink(studentId, parentId)) {
            throw new ParentExceptions.LinkNotFoundException("Link not found");
        }

        Parent parent = findOrThrow(parentId);

        student.getParents().remove(parent);
        studentRepository.saveAndFlush(student);

        if (!studentRepository.existsAnyLinkForParent(parentId)) {
            parentRepository.delete(parent);
        }
    }

    private Parent findOrThrow(Long id) {
        Long schoolId = TenantContext.getSchoolId();
        return parentRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new ParentExceptions.ParentNotFoundException("Parent not found"));
    }

    public List<StudentParentDto> searchParents(String email, String phone) {
        Long schoolId = TenantContext.getSchoolId();

        boolean hasEmail = email != null && !email.isBlank();
        boolean hasPhone = phone != null && !phone.isBlank();

        if (hasEmail == hasPhone) {
            throw new ParentExceptions.InvalidSearchException(
                    "Provide exactly one of email or phone");
        }

        List<Parent> results = hasEmail
                ? parentRepository.searchByEmail(schoolId, email, org.springframework.data.domain.PageRequest.of(0, MAX_SEARCH_RESULTS))
                : parentRepository.searchByPhone(schoolId, phone, org.springframework.data.domain.PageRequest.of(0, MAX_SEARCH_RESULTS));

        return results.stream().map(StudentParentDto::new).toList();
    }

    public ParentDto getByUserId(Long userId) {
        return parentRepository.findByUserId(userId)
                .map(ParentDto::new)
                .orElse(null);
    }

    private String generateTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            password.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }

        return password.toString();
    }

    private void sendWelcomeEmail(String toEmail,
                                  String firstName,
                                  String temporaryPassword,
                                  String schoolName,
                                  School school) {
        String body =
                "Hi " + firstName + ",\n\n" +
                        "An account has been created for you on " + schoolName + ".\n\n" +
                        "Email: " + toEmail + "\n" +
                        "Temporary password: " + temporaryPassword + "\n\n" +
                        "Please log in and change your password immediately.\n\n" +
                        frontendUrl + "/login";
        mailService.send(toEmail, "Your " + schoolName + " account", body, school);
    }
}
