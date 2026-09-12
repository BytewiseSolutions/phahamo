package ls.co.bytewise.api.student;

import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;

    public StudentService(StudentRepository studentRepository, SchoolRepository schoolRepository) {
        this.studentRepository = studentRepository;
        this.schoolRepository = schoolRepository;
    }

    @Transactional
    public StudentDto createStudent(User creator, StudentDto dto) {
        Long schoolId = TenantContext.getSchoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalStateException("Active school not found"));

        Student student = new Student();
        student.setSchool(school);
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setGender(dto.getGender());
        student.setAdmissionNumber(generateAdmissionNumber());
        student.setStatus(StudentStatus.ACTIVE);
        student.setCreatedBy(creator);
        student.setUpdatedBy(creator);

        Student saved = studentRepository.save(student);
        return new StudentDto(saved);
    }

    public List<StudentDto> listStudents() {
        return studentRepository.findAll().stream()
                .map(StudentDto::new)
                .toList();
    }

    public StudentDto getStudent(Long id) {
        return new StudentDto(findOrThrow(id));
    }

    @Transactional
    public StudentDto updateStudent(User editor, Long id, StudentDto dto) {
        Student student = findOrThrow(id);

        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setGender(dto.getGender());
        student.setUpdatedBy(editor);

        if (dto.getStatus() != null) {
            student.setStatus(dto.getStatus());
        }

        Student saved = studentRepository.save(student);
        return new StudentDto(saved);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = findOrThrow(id);
        studentRepository.delete(student);
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
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentExceptions.StudentNotFoundException("Student not found"));
    }
}