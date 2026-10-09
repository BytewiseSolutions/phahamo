package ls.co.bytewise.api.teacher;

import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Transactional
    public TeacherDto createProfileFor(User creator, User user, TeacherDto dto) {
        Long schoolId = TenantContext.getSchoolId();

        if (teacherRepository.findByUserId(user.getId()).isPresent()) {
            throw new TeacherExceptions.TeacherAlreadyExistsException(
                    "This user is already registered as a teacher");
        }

        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacher.setSchool(user.getSchools().stream()
                .filter(s -> s.getId().equals(schoolId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User is not attached to active school")));
        teacher.setQualifications(dto.getQualifications());
        teacher.setDateJoined(dto.getDateJoined());
        teacher.setSpecialization(dto.getSpecialization());
        teacher.setEmploymentType(dto.getEmploymentType());
        teacher.setEmployeeNumber(generateEmployeeNumber(schoolId));
        teacher.setCreatedBy(creator);
        teacher.setUpdatedBy(creator);

        Teacher saved = teacherRepository.save(teacher);
        return new TeacherDto(saved);
    }

    private Teacher getTeacherInCurrentSchool(Long id) {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new IllegalStateException("No active school selected");
        }
        return teacherRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new TeacherExceptions.TeacherNotFoundException("Teacher not found"));
    }

    public TeacherDto getTeacher(Long id) {
        return new TeacherDto(getTeacherInCurrentSchool(id));
    }

    public List<TeacherDto> listTeachers() {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new IllegalStateException("No active school selected");
        }
        return teacherRepository.findAllBySchoolId(schoolId).stream()
                .map(TeacherDto::new)
                .toList();
    }

    @Transactional
    public TeacherDto updateTeacher(User editor, Long id, TeacherDto dto) {
        Teacher teacher = getTeacherInCurrentSchool(id);

        if (dto.getQualifications() != null) {
            teacher.setQualifications(dto.getQualifications());
        }
        if (dto.getDateJoined() != null) {
            teacher.setDateJoined(dto.getDateJoined());
        }
        if (dto.getSpecialization() != null) {
            teacher.setSpecialization(dto.getSpecialization());
        }
        if (dto.getEmploymentType() != null) {
            teacher.setEmploymentType(dto.getEmploymentType());
        }

        teacher.setUpdatedBy(editor);

        Teacher saved = teacherRepository.save(teacher);
        return new TeacherDto(saved);
    }

    private String generateEmployeeNumber(Long schoolId) {
        Teacher lastTeacher = teacherRepository
                .findTopBySchoolIdOrderByEmployeeNumberDesc(schoolId)
                .orElse(null);

        if (lastTeacher == null) {
            return "T-0001";
        }

        String employeeNumber = lastTeacher.getEmployeeNumber();

        if (employeeNumber == null || !employeeNumber.matches("T-\\d+")) {
            throw new IllegalStateException(
                    "Invalid teacher employee number: " + employeeNumber);
        }

        long currentNumber = Long.parseLong(employeeNumber.substring(2));
        return String.format("T-%04d", currentNumber + 1);
    }
}