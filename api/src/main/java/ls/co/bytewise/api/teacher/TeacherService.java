package ls.co.bytewise.api.teacher;

import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.user.UserDto;
import ls.co.bytewise.api.user.UserRepository;
import ls.co.bytewise.api.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public TeacherService(
            TeacherRepository teacherRepository,
            UserRepository userRepository,
            UserService userService) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Transactional
    public TeacherDto createTeacher(User creator, TeacherDto dto) {

        Long schoolId = TenantContext.getSchoolId();

        if (schoolId == null) {
            throw new IllegalStateException("No active school selected");
        }

        if (dto.getUser() == null) {
            throw new IllegalArgumentException("User details are required");
        }

        User user;

        if (dto.getUser().getId() != null) {

            user = userRepository.findById(dto.getUser().getId())
                    .orElseThrow(() ->
                            new TeacherExceptions.TeacherNotFoundException("User not found"));

            boolean belongsToSchool = user.getSchools().stream()
                    .anyMatch(school -> school.getId().equals(schoolId));

            if (!belongsToSchool) {
                throw new TeacherExceptions.TeacherNotFoundException("User not found");
            }

        } else {

            UserDto newUserDto = dto.getUser();
            newUserDto.setRoles(Set.of(UserRole.TEACHER));

            UserDto createdUser = userService.createUser(creator, newUserDto);

            user = userRepository.findById(createdUser.getId())
                    .orElseThrow(() ->
                            new IllegalStateException("Failed to create teacher user"));
        }

        if (teacherRepository.findByUserId(user.getId()).isPresent()) {
            throw new TeacherExceptions.TeacherAlreadyExistsException(
                    "This user is already registered as a teacher"
            );
        }

        Teacher teacher = new Teacher();

        teacher.setUser(user);
        teacher.setQualifications(dto.getQualifications());
        teacher.setDateJoined(dto.getDateJoined());
        teacher.setSpecialization(dto.getSpecialization());
        teacher.setEmploymentType(dto.getEmploymentType());

        teacher.setEmployeeNumber(generateEmployeeNumber(schoolId));

        teacher.setCreatedBy(creator);
        teacher.setUpdatedBy(creator);

        teacher.setSchool(
                creator.getSchools().stream()
                        .filter(school -> school.getId().equals(schoolId))
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException("Active school not found"))
        );

        Teacher saved = teacherRepository.save(teacher);

        return new TeacherDto(saved);
    }

    private Teacher getTeacherInCurrentSchool(Long id) {

        Long schoolId = TenantContext.getSchoolId();

        if (schoolId == null) {
            throw new IllegalStateException("No active school selected");
        }

        return teacherRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() ->
                        new TeacherExceptions.TeacherNotFoundException("Teacher not found"));
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
                    "Invalid teacher employee number: " + employeeNumber
            );
        }

        long currentNumber = Long.parseLong(employeeNumber.substring(2));

        return String.format("T-%04d", currentNumber + 1);
    }
}