package ls.co.bytewise.api.announcement;

import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.mail.MailService;
import ls.co.bytewise.api.parent.Parent;
import ls.co.bytewise.api.parent.ParentRepository;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class AnnouncementService {

    private final AnnouncementRepository repository;
    private final SchoolClassRepository classRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final MailService mailService;

    public AnnouncementService(AnnouncementRepository repository,
                               SchoolClassRepository classRepository,
                               UserRepository userRepository,
                               StudentRepository studentRepository,
                               ParentRepository parentRepository,
                               MailService mailService) {
        this.repository = repository;
        this.classRepository = classRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.parentRepository = parentRepository;
        this.mailService = mailService;
    }

    public List<AnnouncementDto> listForAdmin() {
        Long schoolId = TenantContext.getSchoolId();
        return repository.findAllBySchoolIdOrderByPublishAtDesc(schoolId)
                .stream().map(AnnouncementDto::new).toList();
    }

    public List<AnnouncementDto> getFeedForUser(User currentUser) {
        Long schoolId = TenantContext.getSchoolId();
        List<Long> classIds = new ArrayList<>();

        if (currentUser.hasRole(UserRole.STUDENT)) {
            studentRepository.findByUserId(currentUser.getId())
                    .map(Student::getCurrentClass)
                    .filter(Objects::nonNull)
                    .ifPresent(c -> classIds.add(c.getId()));
        }

        if (currentUser.hasRole(UserRole.PARENT)) {
            parentRepository.findByUserId(currentUser.getId())
                    .ifPresent(parent -> {
                        List<Student> children = studentRepository.findByParentsId(parent.getId());
                        children.stream()
                                .map(Student::getCurrentClass)
                                .filter(Objects::nonNull)
                                .map(SchoolClass::getId)
                                .forEach(classIds::add);
                    });
        }

        if (currentUser.hasRole(UserRole.TEACHER)) {
            List<SchoolClass> teacherClasses = classRepository.findAllByClassTeacherId(currentUser.getId());
            teacherClasses.stream()
                    .map(SchoolClass::getId)
                    .forEach(classIds::add);
        }

        UserRole role = currentUser.getRoles().stream().findFirst().orElse(null);

        return repository.findVisibleForUser(
                schoolId,
                Instant.now(),
                classIds.isEmpty() ? List.of(-1L) : classIds,
                role,
                currentUser.getId()
        ).stream().map(AnnouncementDto::new).toList();
    }

    @Transactional
    public AnnouncementDto create(User creator, AnnouncementPayloadDto dto) {
        Announcement a = new Announcement();
        a.setSchool(creator.getSchools().stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No school linked to user")));
        applyPayload(a, dto);
        a.setAuthor(creator);
        a.setCreatedBy(creator);
        a.setUpdatedBy(creator);

        Announcement saved = repository.save(a);

        if (saved.getStatus() == AnnouncementStatus.PUBLISHED
                && !saved.getPublishAt().isAfter(Instant.now())) {
            sendAnnouncementEmails(saved, creator.getSchools().iterator().next().getId());
        }

        return new AnnouncementDto(saved);
    }

    @Transactional
    public AnnouncementDto update(User editor, Long id, AnnouncementPayloadDto dto) {
        Announcement a = require(id);
        applyPayload(a, dto);
        a.setUpdatedBy(editor);
        return new AnnouncementDto(repository.save(a));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(require(id));
    }

    private void sendAnnouncementEmails(Announcement a, Long schoolId) {
        List<User> recipients = new ArrayList<>();

        if (a.getAudienceType() == AudienceType.ALL) {
            recipients = userRepository.findAllBySchoolId(schoolId);
        } else if (a.getAudienceType() == AudienceType.ROLE) {
            recipients = userRepository.findAllBySchoolIdAndRole(schoolId, a.getTargetRole());
        } else if (a.getAudienceType() == AudienceType.CLASS) {
            List<Student> students = studentRepository.findAllBySchoolIdAndCurrentClassId(
                    schoolId, a.getTargetClass().getId());
            for (Student s : students) {
                if (s.getUser() != null) recipients.add(s.getUser());
                if (s.getParents() != null) {
                    for (Parent p : s.getParents()) {
                        if (p.getUser() != null) recipients.add(p.getUser());
                    }
                }
            }
        } else if (a.getAudienceType() == AudienceType.USER) {
            if (a.getTargetUser() != null) recipients.add(a.getTargetUser());
        }

        Map<Long, User> unique = new LinkedHashMap<>();
        for (User u : recipients) unique.put(u.getId(), u);

        for (User u : unique.values()) {
            if (u.getEmail() == null || u.getEmail().isBlank()) continue;
            String body = "Dear " + u.getFirstName() + ",\n\n"
                    + "A new announcement has been posted:\n\n"
                    + a.getTitle() + "\n\n"
                    + a.getBody() + "\n\n"
                    + "Log in to view more details.\n\n"
                    + a.getSchool().getName();
            mailService.send(u.getEmail(), "New Announcement: " + a.getTitle(), body);
        }
    }

    private void applyPayload(Announcement a, AnnouncementPayloadDto dto) {
        a.setTitle(dto.getTitle());
        a.setBody(dto.getBody());
        a.setStatus(dto.getStatus() != null ? dto.getStatus() : AnnouncementStatus.DRAFT);
        a.setAudienceType(dto.getAudienceType());
        a.setPublishAt(dto.getPublishAt() != null ? dto.getPublishAt() : Instant.now());
        a.setExpiresAt(dto.getExpiresAt());

        a.setTargetClass(null);
        a.setTargetRole(null);
        a.setTargetUser(null);

        if (dto.getAudienceType() == AudienceType.CLASS) {
            SchoolClass schoolClass = classRepository.findById(dto.getTargetClassId())
                    .orElseThrow(() -> new IllegalArgumentException("Class not found"));
            a.setTargetClass(schoolClass);
        } else if (dto.getAudienceType() == AudienceType.ROLE) {
            a.setTargetRole(dto.getTargetRole());
        } else if (dto.getAudienceType() == AudienceType.USER) {
            User targetUser = userRepository.findById(dto.getTargetUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Target user not found"));
            a.setTargetUser(targetUser);
        }
    }

    private Announcement require(Long id) {
        Long schoolId = TenantContext.getSchoolId();
        return repository.findById(id)
                .filter(a -> a.getSchool().getId().equals(schoolId))
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found"));
    }
}