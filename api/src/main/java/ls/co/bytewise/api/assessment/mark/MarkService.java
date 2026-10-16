package ls.co.bytewise.api.assessment.mark;

import ls.co.bytewise.api.assessment.Assessment;
import ls.co.bytewise.api.assessment.AssessmentExceptions;
import ls.co.bytewise.api.assessment.AssessmentRepository;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.result.TermResultRepository;
import ls.co.bytewise.api.result.TermResultStatus;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.student.StudentStatus;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MarkService {

    private final MarkRepository markRepository;
    private final AssessmentRepository assessmentRepository;
    private final StudentRepository studentRepository;
    private final TermResultRepository termResultRepository;

    public MarkService(MarkRepository markRepository,
                       AssessmentRepository assessmentRepository,
                       StudentRepository studentRepository,
                       TermResultRepository termResultRepository) {
        this.markRepository = markRepository;
        this.assessmentRepository = assessmentRepository;
        this.studentRepository = studentRepository;
        this.termResultRepository = termResultRepository;
    }

    public List<MarkDto> listForAssessment(User currentUser, Long assessmentId) {
        Assessment assessment = findAssessment(assessmentId);
        requireAccess(currentUser, assessment);

        Long schoolId = TenantContext.getSchoolId();
        Long classId = assessment.getAssignment().getSchoolClass().getId();

        List<Student> students = studentRepository
                .findAllBySchoolIdAndCurrentClassId(schoolId, classId)
                .stream()
                .filter(s -> s.getStatus() == StudentStatus.ACTIVE)
                .toList();

        List<Mark> marks = markRepository.findAllByAssessmentId(assessmentId);

        List<MarkDto> result = new ArrayList<>();
        for (Student student : students) {
            Mark mark = marks.stream()
                    .filter(m -> m.getStudent().getId().equals(student.getId()))
                    .findFirst()
                    .orElse(null);
            result.add(MarkDto.of(student, mark));
        }

        return result;
    }

    @Transactional
    public List<MarkDto> bulkSave(User currentUser, Long assessmentId, List<MarkDto> rows) {
        Assessment assessment = findAssessment(assessmentId);
        requireAccess(currentUser, assessment);

        Long schoolId = TenantContext.getSchoolId();
        Long classId = assessment.getAssignment().getSchoolClass().getId();
        Long subjectId = assessment.getAssignment().getSubject().getId();
        Long termId = assessment.getTerm().getId();

        List<Student> students = studentRepository
                .findAllBySchoolIdAndCurrentClassId(schoolId, classId);

        for (MarkDto row : rows) {
            if (row.getStudentId() == null) continue;

            boolean locked = termResultRepository
                    .existsByStudentIdAndSubjectIdAndTermIdAndStatus(
                            row.getStudentId(), subjectId, termId, TermResultStatus.FINALISED);

            if (locked) {
                throw new AssessmentExceptions.InvalidAssessmentException(
                        "Results for this subject and term are finalised. Ask an admin to reopen.");
            }
        }

        for (MarkDto row : rows) {
            if (row.getStudentId() == null) continue;

            Optional<Student> studentOpt = students.stream()
                    .filter(s -> s.getId().equals(row.getStudentId()))
                    .findFirst();

            if (studentOpt.isEmpty()) {
                throw new AssessmentExceptions.InvalidAssessmentException(
                        "Student is not in this class: id " + row.getStudentId());
            }

            Student student = studentOpt.get();

            if (row.getScore() != null) {
                if (row.getScore() < 0) {
                    throw new AssessmentExceptions.InvalidAssessmentException(
                            "Mark for " + student.getUser().getFullName() + " cannot be below zero");
                }
                if (row.getScore() > assessment.getMaxMark()) {
                    throw new AssessmentExceptions.InvalidAssessmentException(
                            "Mark for " + student.getUser().getFullName() +
                                    " cannot exceed " + assessment.getMaxMark());
                }
            }

            Mark mark = markRepository
                    .findByAssessmentIdAndStudentId(assessmentId, student.getId())
                    .orElseGet(Mark::new);

            boolean isNew = mark.getId() == null;

            mark.setSchool(assessment.getSchool());
            mark.setAssessment(assessment);
            mark.setStudent(student);
            mark.setScore(row.getScore());
            mark.setUpdatedBy(currentUser);
            if (isNew) {
                mark.setCreatedBy(currentUser);
            }

            markRepository.save(mark);
        }

        return listForAssessment(currentUser, assessmentId);
    }

    private Assessment findAssessment(Long id) {
        Long schoolId = TenantContext.getSchoolId();
        return assessmentRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new AssessmentExceptions.AssessmentNotFoundException("Assessment not found"));
    }

    private void requireAccess(User user, Assessment assessment) {
        boolean isAdmin = user.hasRole(UserRole.SCHOOL_ADMIN)
                || user.hasRole(UserRole.SYSTEM_ADMIN)
                || user.hasRole(UserRole.PRINCIPAL);

        if (isAdmin) return;

        boolean isAssigned = user.hasRole(UserRole.TEACHER)
                && assessment.getAssignment().getTeacher() != null
                && assessment.getAssignment().getTeacher().getId().equals(user.getId());

        if (!isAssigned) {
            throw new AssessmentExceptions.InvalidAssessmentException("You do not have access to this assessment");
        }
    }
}