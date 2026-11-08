package ls.co.bytewise.api.student;

import ls.co.bytewise.api.audit.AuditAction;
import ls.co.bytewise.api.audit.AuditLogService;
import ls.co.bytewise.api.common.DataFile;
import ls.co.bytewise.api.common.DataFileRepository;
import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.studentfee.StudentFeeAccountService;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Set;

import java.time.Year;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final StudentFeeAccountService feeAccountService;
    private final AuditLogService auditLogService;
    private final DataFileRepository dataFileRepository;

    public StudentService(StudentRepository studentRepository,
                           SchoolClassRepository schoolClassRepository,
                           StudentFeeAccountService feeAccountService,
                           AuditLogService auditLogService,
                           DataFileRepository dataFileRepository) {
        this.studentRepository = studentRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.feeAccountService = feeAccountService;
        this.auditLogService = auditLogService;
        this.dataFileRepository = dataFileRepository;
    }

    @Transactional
    public StudentDto createProfileFor(User creator, User user, StudentDto dto) {
        Long schoolId = TenantContext.getSchoolId();
        ensureNoDuplicate(user.getFirstName(), user.getLastName(), dto.getDateOfBirth(), dto.isDuplicateConfirmed());

        Student student = new Student();
        student.setUser(user);
        student.setSchool(user.getSchools().stream()
                .filter(s -> s.getId().equals(schoolId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User is not attached to active school")));
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setGender(dto.getGender());
        student.setAdmissionNumber(generateAdmissionNumber());
        student.setStatus(StudentStatus.ACTIVE);
        student.setCreatedBy(creator);
        student.setUpdatedBy(creator);

        if (dto.getSchoolClassId() != null) {
            student.setCurrentClass(resolveClass(dto.getSchoolClassId(), schoolId));
        }

        Student saved = studentRepository.save(student);
        feeAccountService.synchronizeStudentAccounts(saved, creator);
        return new StudentDto(saved);
    }

    public List<StudentDuplicateDto> findPotentialDuplicates(String firstName,
                                                              String lastName,
                                                              java.time.Instant dateOfBirth) {
        if (firstName == null || lastName == null || dateOfBirth == null) {
            return List.of();
        }
        return studentRepository.findPotentialDuplicates(
                        TenantContext.getSchoolId(), firstName.trim(), lastName.trim(), dateOfBirth)
                .stream()
                .map(StudentDuplicateDto::new)
                .toList();
    }

    public void ensureNoDuplicate(String firstName,
                                  String lastName,
                                  java.time.Instant dateOfBirth,
                                  boolean duplicateConfirmed) {
        if (duplicateConfirmed) return;
        List<StudentDuplicateDto> matches = findPotentialDuplicates(firstName, lastName, dateOfBirth);
        if (!matches.isEmpty()) {
            throw new StudentExceptions.DuplicateStudentException(matches);
        }
    }

    public Page<StudentDto> listStudents(String search, Pageable pageable) {
        Long schoolId = TenantContext.getSchoolId();
        return studentRepository.search(schoolId, search, pageable).map(StudentDto::new);
    }

    public List<StudentDto> listStudentsByClass(Long schoolClassId) {
        Long schoolId = TenantContext.getSchoolId();
        return studentRepository.findAllBySchoolIdAndCurrentClassId(schoolId, schoolClassId).stream()
                .map(StudentDto::new)
                .toList();
    }

    public StudentDto getStudent(Long id) {
        return new StudentDto(findOrThrow(id));
    }

    @Transactional
    public StudentDto uploadPhoto(User editor, Long id, MultipartFile file) throws IOException {
        Student student = findOrThrow(id);
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("A student photo is required");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("Student photos must be 2MB or smaller");
        }
        String contentType = file.getContentType();
        Set<String> allowedTypes = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
        if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Only JPG, PNG, WEBP and GIF images are accepted");
        }

        DataFile previous = student.getPhoto();
        DataFile photo = DataFile.builder()
                .name(file.getOriginalFilename() == null ? "student-photo" : file.getOriginalFilename())
                .type(contentType)
                .contents(file.getBytes())
                .fileSize(file.getSize())
                .build();
        photo.setCreatedBy(editor);
        DataFile savedPhoto = dataFileRepository.save(photo);
        student.setPhoto(savedPhoto);
        student.setUpdatedBy(editor);
        Student saved = studentRepository.save(student);
        if (previous != null) dataFileRepository.delete(previous);
        return new StudentDto(saved);
    }

    @Transactional
    public void removePhoto(User editor, Long id) {
        Student student = findOrThrow(id);
        DataFile previous = student.getPhoto();
        if (previous == null) return;
        student.setPhoto(null);
        student.setUpdatedBy(editor);
        studentRepository.save(student);
        dataFileRepository.delete(previous);
    }

    @Transactional(readOnly = true)
    public DataFile getPhoto(Long id) {
        DataFile photo = findOrThrow(id).getPhoto();
        if (photo == null) throw new IllegalArgumentException("Student has no photo");
        return photo;
    }

    @Transactional
    public StudentDto updateStudent(User editor, Long id, StudentDto dto) {
        Student student = findOrThrow(id);

        student.setDateOfBirth(dto.getDateOfBirth());
        student.setGender(dto.getGender());
        student.setUpdatedBy(editor);

        Student saved = studentRepository.save(student);
        feeAccountService.synchronizeStudentAccounts(saved, editor);
        return new StudentDto(saved);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = findOrThrow(id);
        studentRepository.delete(student);
    }

    @Transactional
    public StudentDto assignClass(User editor, Long studentId, StudentClassUpdateDto dto) {
        Long schoolId = TenantContext.getSchoolId();
        Student student = findOrThrow(studentId);
        StudentStatus previousStatus = student.getStatus();
        Long schoolClassId = dto.getSchoolClassId();

        if (schoolClassId == null) {
            student.setCurrentClass(null);
        } else {
            SchoolClass targetClass = resolveClass(schoolClassId, schoolId);
            StudentClassFeePreviewDto preview = feeAccountService.previewClassChange(student, targetClass);
            if (preview.isFeesChanged() && !dto.isConfirmFeeChange()) {
                throw new StudentExceptions.FeeChangeConfirmationRequiredException(preview);
            }
            student.setCurrentClass(targetClass);
            // Assigning a student to a current class re-enrols them in the school.
            // This also restores students previously marked as COMPLETED.
            student.setStatus(StudentStatus.ACTIVE);
            feeAccountService.applyClassChange(student, targetClass, editor);
        }

        student.setUpdatedBy(editor);

        Student saved = studentRepository.save(student);
        recordStatusChange(editor, saved, previousStatus);
        if (schoolClassId == null) {
            feeAccountService.synchronizeStudentAccounts(saved, editor);
        }
        return new StudentDto(saved);
    }

    @Transactional(readOnly = true)
    public StudentClassFeePreviewDto previewClassChange(Long studentId, Long schoolClassId) {
        Student student = findOrThrow(studentId);
        return feeAccountService.previewClassChange(student, resolveClass(schoolClassId, TenantContext.getSchoolId()));
    }

    @Transactional
    public StudentDto reactivate(User editor, Long studentId) {
        Student student = findOrThrow(studentId);
        StudentStatus previousStatus = student.getStatus();

        if (student.getCurrentClass() == null) {
            throw new StudentExceptions.InvalidStudentOperationException(
                    "Assign the student to a class before reactivating them");
        }

        student.setStatus(StudentStatus.ACTIVE);
        student.setUpdatedBy(editor);
        Student saved = studentRepository.save(student);
        recordStatusChange(editor, saved, previousStatus);
        feeAccountService.synchronizeStudentAccounts(saved, editor);
        return new StudentDto(saved);
    }

    private void recordStatusChange(User editor, Student student, StudentStatus previousStatus) {
        if (previousStatus == student.getStatus()) return;
        auditLogService.record(student.getSchool(), editor, AuditAction.STUDENT_STATUS_CHANGED,
                "STUDENT", student.getId(),
                "Changed status for " + student.getUser().getFullName() + " from "
                        + previousStatus + " to " + student.getStatus());
    }

    private SchoolClass resolveClass(Long schoolClassId, Long schoolId) {
        SchoolClass schoolClass = schoolClassRepository.findById(schoolClassId)
                .orElseThrow(() -> new StudentExceptions.InvalidStudentOperationException("Class not found"));

        if (!schoolClass.getSchool().getId().equals(schoolId)) {
            throw new StudentExceptions.InvalidStudentOperationException("Class does not belong to the active school");
        }

        return schoolClass;
    }

    private String generateAdmissionNumber() {
        String yearPrefix = Year.now().getValue() + "-";
        long sequence = studentRepository.countByAdmissionNumberStartingWith(yearPrefix);

        String candidate;
        do {
            sequence++;
            candidate = yearPrefix + String.format("%06d", sequence);
        } while (studentRepository.existsByAdmissionNumber(candidate));

        return candidate;
    }

    private Student findOrThrow(Long id) {
        Long schoolId = TenantContext.getSchoolId();
        return studentRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new StudentExceptions.StudentNotFoundException("Student not found"));
    }

    public StudentDto getByUserId(Long userId) {
        return studentRepository.findByUserId(userId)
                .map(StudentDto::new)
                .orElse(null);
    }
}
