package ls.co.bytewise.api.student;

import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.status.StudentStatusChange;
import ls.co.bytewise.api.student.status.StudentStatusChangeDto;
import ls.co.bytewise.api.student.status.StudentStatusChangeRepository;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final StudentStatusChangeRepository statusChangeRepository;

    public StudentService(StudentRepository studentRepository,
                          SchoolClassRepository schoolClassRepository,
                          StudentStatusChangeRepository statusChangeRepository) {
        this.studentRepository = studentRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.statusChangeRepository = statusChangeRepository;
    }

    @Transactional
    public StudentDto createProfileFor(User creator, User user, StudentDto dto) {
        Long schoolId = TenantContext.getSchoolId();

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
        return new StudentDto(saved);
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
    public StudentDto updateStudent(User editor, Long id, StudentDto dto) {
        Student student = findOrThrow(id);

        student.setDateOfBirth(dto.getDateOfBirth());
        student.setGender(dto.getGender());
        student.setUpdatedBy(editor);

        Student saved = studentRepository.save(student);
        return new StudentDto(saved);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = findOrThrow(id);
        studentRepository.delete(student);
    }

    @Transactional
    public StudentDto assignClass(User editor, Long studentId, Long schoolClassId) {
        Long schoolId = TenantContext.getSchoolId();
        Student student = findOrThrow(studentId);

        if (schoolClassId == null) {
            student.setCurrentClass(null);
        } else {
            student.setCurrentClass(resolveClass(schoolClassId, schoolId));
        }

        student.setUpdatedBy(editor);

        Student saved = studentRepository.save(student);
        return new StudentDto(saved);
    }

    @Transactional
    public StudentDto changeStatus(User editor, Long studentId, StudentStatus newStatus, String reason) {
        Student student = findOrThrow(studentId);

        if (student.getStatus() == newStatus) {
            throw new StudentExceptions.InvalidStudentOperationException(
                    "Student already has status " + newStatus);
        }

        StudentStatusChange change = new StudentStatusChange();
        change.setSchool(student.getSchool());
        change.setStudent(student);
        change.setPreviousStatus(student.getStatus());
        change.setNewStatus(newStatus);
        change.setReason(reason);
        change.setCreatedBy(editor);
        change.setUpdatedBy(editor);
        statusChangeRepository.save(change);

        student.setStatus(newStatus);
        student.setUpdatedBy(editor);

        Student saved = studentRepository.save(student);
        return new StudentDto(saved);
    }

    public List<StudentStatusChangeDto> getStatusHistory(Long studentId) {
        findOrThrow(studentId);
        return statusChangeRepository.findAllByStudentIdOrderByCreatedAtDesc(studentId).stream()
                .map(StudentStatusChangeDto::new)
                .toList();
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