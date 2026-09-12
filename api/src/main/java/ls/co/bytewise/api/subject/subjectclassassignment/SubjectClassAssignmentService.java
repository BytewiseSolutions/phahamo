package ls.co.bytewise.api.subject.subjectclassassignment;

import ls.co.bytewise.api.academicyear.AcademicYear;
import ls.co.bytewise.api.academicyear.AcademicYearRepository;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.subject.Subject;
import ls.co.bytewise.api.subject.SubjectRepository;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectClassAssignmentService {

    private final SubjectClassAssignmentRepository assignmentRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final UserRepository userRepository;

    public SubjectClassAssignmentService(SubjectClassAssignmentRepository assignmentRepository,
                                         AcademicYearRepository academicYearRepository,
                                         SubjectRepository subjectRepository,
                                         SchoolClassRepository schoolClassRepository,
                                         UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.academicYearRepository = academicYearRepository;
        this.subjectRepository = subjectRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SubjectClassAssignmentDto createAssignment(User creator, SubjectClassAssignmentDto dto) {
        Long schoolId = TenantContext.getSchoolId();

        AcademicYear academicYear = academicYearRepository.findById(dto.getAcademicYearId())
                .orElseThrow(() -> new SubjectClassAssignmentExceptions.InvalidAssignmentException("Academic year not found"));
        validateBelongsToSchool(academicYear.getSchool().getId(), schoolId, "Academic year");

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new SubjectClassAssignmentExceptions.InvalidAssignmentException("Subject not found"));
        validateBelongsToSchool(subject.getSchool().getId(), schoolId, "Subject");

        SchoolClass schoolClass = schoolClassRepository.findById(dto.getSchoolClassId())
                .orElseThrow(() -> new SubjectClassAssignmentExceptions.InvalidAssignmentException("Class not found"));
        validateBelongsToSchool(schoolClass.getSchool().getId(), schoolId, "Class");

        if (assignmentRepository.existsBySubjectIdAndSchoolClassIdAndAcademicYearId(
                subject.getId(), schoolClass.getId(), academicYear.getId())) {
            throw new SubjectClassAssignmentExceptions.DuplicateAssignmentException(
                    "This subject is already assigned to this class for the selected academic year");
        }

        SubjectClassAssignment assignment = new SubjectClassAssignment();
        assignment.setSchool(academicYear.getSchool());
        assignment.setAcademicYear(academicYear);
        assignment.setSubject(subject);
        assignment.setSchoolClass(schoolClass);
        assignment.setCreatedBy(creator);
        assignment.setUpdatedBy(creator);

        assignTeacher(assignment, dto.getTeacherId(), schoolId);

        SubjectClassAssignment saved = assignmentRepository.save(assignment);
        return new SubjectClassAssignmentDto(saved);
    }

    public List<SubjectClassAssignmentDto> listAssignments() {
        return assignmentRepository.findAll().stream()
                .map(SubjectClassAssignmentDto::new)
                .toList();
    }

    public List<SubjectClassAssignmentDto> listByClass(Long schoolClassId) {
        return assignmentRepository.findAllBySchoolClassId(schoolClassId).stream()
                .map(SubjectClassAssignmentDto::new)
                .toList();
    }

    public List<SubjectClassAssignmentDto> listBySubject(Long subjectId) {
        return assignmentRepository.findAllBySubjectId(subjectId).stream()
                .map(SubjectClassAssignmentDto::new)
                .toList();
    }

    public SubjectClassAssignmentDto getAssignment(Long id) {
        return new SubjectClassAssignmentDto(findOrThrow(id));
    }

    @Transactional
    public SubjectClassAssignmentDto updateAssignment(User editor, Long id, SubjectClassAssignmentDto dto) {
        Long schoolId = TenantContext.getSchoolId();
        SubjectClassAssignment assignment = findOrThrow(id);

        AcademicYear academicYear = academicYearRepository.findById(dto.getAcademicYearId())
                .orElseThrow(() -> new SubjectClassAssignmentExceptions.InvalidAssignmentException("Academic year not found"));
        validateBelongsToSchool(academicYear.getSchool().getId(), schoolId, "Academic year");

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new SubjectClassAssignmentExceptions.InvalidAssignmentException("Subject not found"));
        validateBelongsToSchool(subject.getSchool().getId(), schoolId, "Subject");

        SchoolClass schoolClass = schoolClassRepository.findById(dto.getSchoolClassId())
                .orElseThrow(() -> new SubjectClassAssignmentExceptions.InvalidAssignmentException("Class not found"));
        validateBelongsToSchool(schoolClass.getSchool().getId(), schoolId, "Class");

        if (assignmentRepository.existsBySubjectIdAndSchoolClassIdAndAcademicYearIdAndIdNot(
                subject.getId(), schoolClass.getId(), academicYear.getId(), id)) {
            throw new SubjectClassAssignmentExceptions.DuplicateAssignmentException(
                    "This subject is already assigned to this class for the selected academic year");
        }

        assignment.setAcademicYear(academicYear);
        assignment.setSubject(subject);
        assignment.setSchoolClass(schoolClass);
        assignment.setUpdatedBy(editor);

        assignTeacher(assignment, dto.getTeacherId(), schoolId);

        SubjectClassAssignment saved = assignmentRepository.save(assignment);
        return new SubjectClassAssignmentDto(saved);
    }

    @Transactional
    public void deleteAssignment(Long id) {
        SubjectClassAssignment assignment = findOrThrow(id);
        assignmentRepository.delete(assignment);
    }

    private void assignTeacher(SubjectClassAssignment assignment, Long teacherId, Long schoolId) {
        if (teacherId == null) {
            assignment.setTeacher(null);
            return;
        }

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new SubjectClassAssignmentExceptions.InvalidAssignmentException("Teacher not found"));

        boolean belongsToSchool = teacher.getSchools().stream()
                .anyMatch(school -> school.getId().equals(schoolId));

        if (!belongsToSchool) {
            throw new SubjectClassAssignmentExceptions.InvalidAssignmentException("Teacher does not belong to the active school");
        }

        if (!teacher.hasRole(UserRole.TEACHER)) {
            throw new SubjectClassAssignmentExceptions.InvalidAssignmentException("Assigned user must have the TEACHER role");
        }

        assignment.setTeacher(teacher);
    }

    private void validateBelongsToSchool(Long entitySchoolId, Long activeSchoolId, String entityName) {
        if (!entitySchoolId.equals(activeSchoolId)) {
            throw new SubjectClassAssignmentExceptions.InvalidAssignmentException(entityName + " does not belong to the active school");
        }
    }

    private SubjectClassAssignment findOrThrow(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new SubjectClassAssignmentExceptions.AssignmentNotFoundException("Assignment not found"));
    }
}