package ls.co.bytewise.api.student.promotion;

import ls.co.bytewise.api.audit.AuditAction;
import ls.co.bytewise.api.audit.AuditLogService;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentDto;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.student.StudentStatus;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PromotionService {

    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final PromotionRepository promotionRepository;
    private final AuditLogService auditLogService;

    public PromotionService(StudentRepository studentRepository,
                            SchoolClassRepository schoolClassRepository,
                            PromotionRepository promotionRepository,
                            AuditLogService auditLogService) {
        this.studentRepository = studentRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.promotionRepository = promotionRepository;
        this.auditLogService = auditLogService;
    }

    public List<StudentDto> preview(Long fromClassId) {
        Long schoolId = TenantContext.getSchoolId();
        requireClass(fromClassId, schoolId);

        return studentRepository.findAllBySchoolIdAndCurrentClassId(schoolId, fromClassId)
                .stream()
                .filter(s -> s.getStatus() == StudentStatus.ACTIVE)
                .sorted(Comparator.comparing(s -> s.getUser().getLastName()))
                .map(StudentDto::new)
                .toList();
    }

    @Transactional
    public String promote(User admin, PromotionPayloadDto req) {
        Long schoolId = TenantContext.getSchoolId();

        if (req.getStudentIds() == null || req.getStudentIds().isEmpty()) {
            throw new IllegalArgumentException("No students selected");
        }
        if (req.getAction() == null) {
            throw new IllegalArgumentException("Action is required");
        }
        if (req.getReason() == null || req.getReason().isBlank()) {
            throw new IllegalArgumentException("Reason is required");
        }

        SchoolClass fromClass = requireClass(req.getFromClassId(), schoolId);
        SchoolClass toClass = null;

        if (req.getAction() == PromotionAction.PROMOTE) {
            if (req.getToClassId() == null) {
                throw new IllegalArgumentException("Target class is required");
            }
            toClass = requireClass(req.getToClassId(), schoolId);
            if (toClass.getId().equals(fromClass.getId())) {
                throw new IllegalArgumentException("Target class must be different from source class");
            }
        }

        Set<Long> uniqueIds = new LinkedHashSet<>(req.getStudentIds());
        List<Student> students = studentRepository.findAllById(uniqueIds);

        if (students.size() != uniqueIds.size()) {
            throw new IllegalArgumentException("One or more students were not found");
        }

        for (Student student : students) {
            if (!student.getSchool().getId().equals(schoolId)) {
                throw new IllegalArgumentException(
                        "Student not in this school: " + student.getAdmissionNumber());
            }
            if (student.getCurrentClass() == null
                    || !student.getCurrentClass().getId().equals(fromClass.getId())) {
                throw new IllegalArgumentException(
                        "Student " + student.getAdmissionNumber() + " is not in the source class");
            }
            if (student.getStatus() != StudentStatus.ACTIVE) {
                throw new IllegalArgumentException(
                        "Student " + student.getAdmissionNumber() + " is not active");
            }
        }

        String batchId = UUID.randomUUID().toString();
        Instant now = Instant.now();

        for (Student student : students) {
            StudentStatus previousStatus = student.getStatus();
            StudentStatus newStatus;

            if (req.getAction() == PromotionAction.PROMOTE) {
                student.setCurrentClass(toClass);
                newStatus = StudentStatus.ACTIVE;
            } else {
                student.setCurrentClass(null);
                student.setStatus(StudentStatus.COMPLETED);
                newStatus = StudentStatus.COMPLETED;
            }
            student.setUpdatedBy(admin);
            studentRepository.save(student);

            auditLogService.record(student.getSchool(), admin, AuditAction.STUDENT_STATUS_CHANGED,
                    "STUDENT", student.getId(),
                    "Changed status for " + student.getUser().getFullName() + " from "
                            + previousStatus + " to " + newStatus + "; reason: " + req.getReason().trim());

            Promotion log = new Promotion();
            log.setStudent(student);
            log.setFromClass(fromClass);
            log.setToClass(toClass);
            log.setPreviousStatus(previousStatus);
            log.setNewStatus(newStatus);
            log.setReason(req.getReason().trim());
            log.setPromotedAt(now);
            log.setPromotedBy(admin);
            log.setBatchId(batchId);
            log.setSchool(fromClass.getSchool());
            log.setCreatedBy(admin);
            log.setUpdatedBy(admin);
            promotionRepository.save(log);
        }

        return batchId;
    }

    private SchoolClass requireClass(Long id, Long schoolId) {
        return schoolClassRepository.findById(id)
                .filter(c -> c.getSchool().getId().equals(schoolId))
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));
    }
    public List<PromotionDto> historyForSchool(Long schoolId) {
        return promotionRepository.findAllBySchoolIdOrderByPromotedAtDesc(schoolId)
                .stream()
                .map(PromotionDto::new)
                .toList();
    }

    public List<PromotionDto> historyForStudent(Long schoolId, Long studentId) {
        return promotionRepository.findAllBySchoolIdAndStudentIdOrderByPromotedAtDesc(schoolId, studentId)
                .stream()
                .map(PromotionDto::new)
                .toList();
    }
}
