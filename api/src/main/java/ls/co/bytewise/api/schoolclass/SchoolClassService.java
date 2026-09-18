package ls.co.bytewise.api.schoolclass;

import ls.co.bytewise.api.academicyear.AcademicYear;
import ls.co.bytewise.api.academicyear.AcademicYearRepository;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;
    private final AcademicYearRepository academicYearRepository;
    private final UserRepository userRepository;

    public SchoolClassService(SchoolClassRepository schoolClassRepository,
                              AcademicYearRepository academicYearRepository,
                              UserRepository userRepository) {
        this.schoolClassRepository = schoolClassRepository;
        this.academicYearRepository = academicYearRepository;
        this.userRepository = userRepository;
    }

 @Transactional
public SchoolClassDto createClass(User creator, SchoolClassDto dto) {
    Long schoolId = TenantContext.getSchoolId();

    AcademicYear academicYear = academicYearRepository.findById(dto.getAcademicYearId())
            .orElseThrow(() -> new SchoolClassExceptions.InvalidSchoolClassException("Academic year not found"));

    if (!academicYear.getSchool().getId().equals(schoolId)) {
        throw new SchoolClassExceptions.InvalidSchoolClassException("Academic year does not belong to the active school");
    }

    if (schoolClassRepository.existsByNameAndAcademicYear_Id(dto.getName(), academicYear.getId())) {
        throw new SchoolClassExceptions.DuplicateClassNameException(
                "A class with this name already exists in the selected academic year");
    }

    SchoolClass schoolClass = new SchoolClass();
    schoolClass.setSchool(academicYear.getSchool());
    schoolClass.setAcademicYear(academicYear);
    schoolClass.setName(dto.getName());
    schoolClass.setGradeLevel(dto.getGradeLevel());
    schoolClass.setCreatedBy(creator);
    schoolClass.setUpdatedBy(creator);

    assignClassTeacher(schoolClass, dto.getClassTeacherId(), schoolId);

    SchoolClass saved = schoolClassRepository.save(schoolClass);
    return new SchoolClassDto(saved);
}

    public List<SchoolClassDto> listClasses() {
        Long schoolId = TenantContext.getSchoolId();
        return schoolClassRepository.findAllBySchool_Id(schoolId).stream()
                .map(SchoolClassDto::new)
                .toList();
    }

    public SchoolClassDto getClass(Long id) {
        return new SchoolClassDto(findOrThrow(id));
    }

 @Transactional
public SchoolClassDto updateClass(User editor, Long id, SchoolClassDto dto) {
    Long schoolId = TenantContext.getSchoolId();
    SchoolClass schoolClass = findOrThrow(id);

    AcademicYear academicYear = academicYearRepository.findById(dto.getAcademicYearId())
            .orElseThrow(() -> new SchoolClassExceptions.InvalidSchoolClassException("Academic year not found"));

    if (!academicYear.getSchool().getId().equals(schoolId)) {
        throw new SchoolClassExceptions.InvalidSchoolClassException("Academic year does not belong to the active school");
    }

    if (schoolClassRepository.existsByNameAndAcademicYear_IdAndIdNot(dto.getName(), academicYear.getId(), id)) {
        throw new SchoolClassExceptions.DuplicateClassNameException(
                "A class with this name already exists in the selected academic year");
    }

    schoolClass.setAcademicYear(academicYear);
    schoolClass.setName(dto.getName());
    schoolClass.setGradeLevel(dto.getGradeLevel());
    schoolClass.setUpdatedBy(editor);

    assignClassTeacher(schoolClass, dto.getClassTeacherId(), schoolId);

    SchoolClass saved = schoolClassRepository.save(schoolClass);
    return new SchoolClassDto(saved);
}

    @Transactional
    public void deleteClass(Long id) {
        SchoolClass schoolClass = findOrThrow(id);
        schoolClassRepository.delete(schoolClass);
    }

    private void assignClassTeacher(SchoolClass schoolClass, Long classTeacherId, Long schoolId) {
        if (classTeacherId == null) {
            schoolClass.setClassTeacher(null);
            return;
        }

        User teacher = userRepository.findById(classTeacherId)
                .orElseThrow(() -> new SchoolClassExceptions.InvalidSchoolClassException("Class teacher not found"));

        boolean belongsToSchool = teacher.getSchools().stream()
                .anyMatch(school -> school.getId().equals(schoolId));

        if (!belongsToSchool) {
            throw new SchoolClassExceptions.InvalidSchoolClassException("Class teacher does not belong to the active school");
        }

        if (!teacher.hasRole(UserRole.TEACHER)) {
            throw new SchoolClassExceptions.InvalidSchoolClassException("Assigned user must have the TEACHER role");
        }

        schoolClass.setClassTeacher(teacher);
    }

    private SchoolClass findOrThrow(Long id) {
        Long schoolId = TenantContext.getSchoolId();
        return schoolClassRepository.findByIdAndSchool_Id(id, schoolId)
                .orElseThrow(() -> new SchoolClassExceptions.SchoolClassNotFoundException("Class not found"));
    }

    public SchoolClassDto getMyClass(User teacher) {
        Long schoolId = TenantContext.getSchoolId();

        return schoolClassRepository.findByClassTeacher_IdAndSchool_Id(teacher.getId(), schoolId)
                .map(SchoolClassDto::new)
                .orElseThrow(() -> new SchoolClassExceptions.SchoolClassNotFoundException(
                        "You are not assigned as a class teacher"));
    }
}