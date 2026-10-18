package ls.co.bytewise.api.result.remark;

import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.result.TermResultRepository;
import ls.co.bytewise.api.result.TermResultStatus;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.subject.subjectclassassignment.SubjectClassAssignmentRepository;
import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.term.TermRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class RemarkService {

    private final RemarkRepository repository;
    private final StudentRepository studentRepository;
    private final SchoolClassRepository classRepository;
    private final TermRepository termRepository;
    private final TermResultRepository termResultRepository;
    private final SubjectClassAssignmentRepository assignmentRepository;

    public RemarkService(RemarkRepository repository,
                         StudentRepository studentRepository,
                         SchoolClassRepository classRepository,
                         TermRepository termRepository,
                         TermResultRepository termResultRepository,
                         SubjectClassAssignmentRepository assignmentRepository) {
        this.repository = repository;
        this.studentRepository = studentRepository;
        this.classRepository = classRepository;
        this.termRepository = termRepository;
        this.termResultRepository = termResultRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public List<RemarkDto> listForClass(Long classId, Long termId, User currentUser) {
        Long schoolId = TenantContext.getSchoolId();

        SchoolClass schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        boolean isClassTeacher = schoolClass.getClassTeacher() != null
                && schoolClass.getClassTeacher().getId().equals(currentUser.getId());
        boolean locked = isTermFinalisedForClass(schoolId, classId, termId);

        List<Student> students = studentRepository.findAllBySchoolIdAndCurrentClassId(schoolId, classId);

        List<RemarkDto> result = new ArrayList<>();
        for (Student student : students) {
            Remark remark = repository
                    .findByStudentIdAndTermIdAndSchoolId(student.getId(), termId, schoolId)
                    .orElseGet(() -> buildEmpty(student, termId, schoolId));

            result.add(new RemarkDto(remark, locked, isClassTeacher));
        }
        return result;
    }

    @Transactional
    public void saveClassTeacherRemark(User currentUser, Long studentId, Long termId, String remark) {
        Long schoolId = TenantContext.getSchoolId();
        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        SchoolClass schoolClass = student.getCurrentClass();
        if (schoolClass == null || schoolClass.getClassTeacher() == null) {
            throw new IllegalArgumentException("Student has no assigned class teacher");
        }
        if (!schoolClass.getClassTeacher().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Only the class teacher can write this remark");
        }

        if (isTermFinalisedForClass(schoolId, schoolClass.getId(), termId)) {
            throw new IllegalArgumentException("Remarks are locked because the term's results are finalised");
        }

        Remark entity = requireOrCreate(student, termId, schoolId);
        entity.setClassTeacherRemark(remark);
        entity.setClassTeacherRemarkBy(currentUser);
        entity.setClassTeacherRemarkAt(Instant.now());
        entity.setUpdatedBy(currentUser);
        repository.save(entity);
    }

    @Transactional
    public void savePrincipalRemark(User currentUser, Long studentId, Long termId, String remark) {
        Long schoolId = TenantContext.getSchoolId();
        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        SchoolClass schoolClass = student.getCurrentClass();
        if (schoolClass == null) {
            throw new IllegalArgumentException("Student has no current class");
        }

        if (!currentUser.hasRole(UserRole.PRINCIPAL) && !currentUser.hasRole(UserRole.SCHOOL_ADMIN)) {
            throw new IllegalArgumentException("Only the principal can write this remark");
        }

        if (isTermFinalisedForClass(schoolId, schoolClass.getId(), termId)) {
            throw new IllegalArgumentException("Remarks are locked because the term's results are finalised");
        }

        Remark entity = requireOrCreate(student, termId, schoolId);
        entity.setPrincipalRemark(remark);
        entity.setPrincipalRemarkBy(currentUser);
        entity.setPrincipalRemarkAt(Instant.now());
        entity.setUpdatedBy(currentUser);
        repository.save(entity);
    }

    public boolean isTermFinalisedForClass(Long schoolId, Long classId, Long termId) {
        long totalSubjects = assignmentRepository.countBySchoolClassIdAndSchoolId(classId, schoolId);
        long finalisedSubjects = termResultRepository
                .countBySchoolClassIdAndTermIdAndSchoolIdAndStatus(classId, termId, schoolId, TermResultStatus.FINALISED);
        return totalSubjects > 0 && totalSubjects == finalisedSubjects;
    }

    public Remark getOrCreate(Student student, Long termId, Long schoolId) {
        return repository.findByStudentIdAndTermIdAndSchoolId(student.getId(), termId, schoolId)
                .orElseGet(() -> buildEmpty(student, termId, schoolId));
    }

    private Remark requireOrCreate(Student student, Long termId, Long schoolId) {
        return repository.findByStudentIdAndTermIdAndSchoolId(student.getId(), termId, schoolId)
                .orElseGet(() -> {
                    Term term = termRepository.findByIdAndSchoolId(termId, schoolId)
                            .orElseThrow(() -> new IllegalArgumentException("Term not found"));
                    Remark entity = new Remark();
                    entity.setStudent(student);
                    entity.setTerm(term);
                    entity.setSchool(student.getSchool());
                    return entity;
                });
    }

    private Remark buildEmpty(Student student, Long termId, Long schoolId) {
        Term term = termRepository.findByIdAndSchoolId(termId, schoolId)
                .orElseThrow(() -> new IllegalArgumentException("Term not found"));
        Remark entity = new Remark();
        entity.setStudent(student);
        entity.setTerm(term);
        entity.setSchool(student.getSchool());
        entity.setId(null);
        return entity;
    }
}