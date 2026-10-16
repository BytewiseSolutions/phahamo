package ls.co.bytewise.api.assessment;

import ls.co.bytewise.api.assessment.mark.MarkRepository;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.examination.Examination;
import ls.co.bytewise.api.examination.ExaminationRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.subject.subjectclassassignment.SubjectClassAssignment;
import ls.co.bytewise.api.subject.subjectclassassignment.SubjectClassAssignmentRepository;
import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.term.TermRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final SubjectClassAssignmentRepository assignmentRepository;
    private final TermRepository termRepository;
    private final MarkRepository markRepository;
    private final ExaminationRepository examinationRepository;

    public AssessmentService(AssessmentRepository assessmentRepository,
                             SubjectClassAssignmentRepository assignmentRepository,
                             TermRepository termRepository,
                             MarkRepository markRepository,
                             ExaminationRepository examinationRepository) {
        this.assessmentRepository = assessmentRepository;
        this.assignmentRepository = assignmentRepository;
        this.termRepository = termRepository;
        this.markRepository = markRepository;
        this.examinationRepository = examinationRepository;
    }

    @Transactional
    public AssessmentDto create(User currentUser, AssessmentDto dto) {
        Long schoolId = TenantContext.getSchoolId();

        SubjectClassAssignment assignment = assignmentRepository.findById(dto.getAssignmentId())
                .orElseThrow(() -> new AssessmentExceptions.InvalidAssessmentException("Assignment not found"));

        if (!assignment.getSchool().getId().equals(schoolId)) {
            throw new AssessmentExceptions.InvalidAssessmentException("Assignment does not belong to the active school");
        }

        if (assignment.getTeacher() == null || !assignment.getTeacher().getId().equals(currentUser.getId())) {
            throw new AssessmentExceptions.InvalidAssessmentException(
                    "You are not the teacher assigned to this subject and class");
        }

        Term term = termRepository.findById(dto.getTermId())
                .orElseThrow(() -> new AssessmentExceptions.InvalidAssessmentException("Term not found"));

        if (!term.getSchool().getId().equals(schoolId)) {
            throw new AssessmentExceptions.InvalidAssessmentException("Term does not belong to the active school");
        }

        if (!term.getAcademicYear().getId().equals(assignment.getAcademicYear().getId())) {
            throw new AssessmentExceptions.InvalidAssessmentException(
                    "Term and assignment belong to different academic years");
        }

        validate(dto);

        Assessment assessment = new Assessment();

        AssessmentType type = dto.getType() != null ? dto.getType() : AssessmentType.CONTINUOUS;
        assessment.setType(type);

        if (type == AssessmentType.EXAM) {
            if (dto.getExaminationId() == null) {
                throw new AssessmentExceptions.InvalidAssessmentException(
                        "Examination is required for exam assessments");
            }
            Examination examination = examinationRepository.findById(dto.getExaminationId())
                    .orElseThrow(() -> new AssessmentExceptions.InvalidAssessmentException("Examination not found"));

            if (!examination.getSchool().getId().equals(schoolId)) {
                throw new AssessmentExceptions.InvalidAssessmentException(
                        "Examination does not belong to the active school");
            }

            if (!examination.getTerm().getId().equals(term.getId())) {
                throw new AssessmentExceptions.InvalidAssessmentException(
                        "Examination and assessment must belong to the same term");
            }

            boolean coversSubjectClass = examination.getSubjectClasses().stream()
                    .anyMatch(sc -> sc.getSubject().getId().equals(assignment.getSubject().getId())
                            && sc.getSchoolClass().getId().equals(assignment.getSchoolClass().getId()));

            if (!coversSubjectClass) {
                throw new AssessmentExceptions.InvalidAssessmentException(
                        "Examination does not cover this subject and class");
            }

            assessment.setExamination(examination);

            int existingWeight = assessmentRepository
                    .findAllByAssignment_SchoolClass_IdAndAssignment_Subject_IdAndTermIdAndSchoolId(
                            assignment.getSchoolClass().getId(),
                            assignment.getSubject().getId(),
                            term.getId(),
                            schoolId)
                    .stream()
                    .filter(a -> a.getType() == AssessmentType.EXAM)
                    .mapToInt(Assessment::getWeight)
                    .sum();

            if (existingWeight + dto.getWeight() > 100) {
                throw new AssessmentExceptions.InvalidAssessmentException(
                        "Adding this exam would make the total weight " + (existingWeight + dto.getWeight())
                                + ". Exam weights for a subject must not exceed 100.");
            }
        } else {
            assessment.setExamination(null);
        }

        assessment.setSchool(assignment.getSchool());
        assessment.setAssignment(assignment);
        assessment.setTerm(term);
        assessment.setName(dto.getName().trim());
        assessment.setMaxMark(dto.getMaxMark());
        assessment.setWeight(dto.getWeight());
        assessment.setDate(dto.getDate());
        assessment.setCreatedBy(currentUser);
        assessment.setUpdatedBy(currentUser);

        Assessment saved = assessmentRepository.save(assessment);
        return new AssessmentDto(saved);
    }

    public List<AssessmentDto> listAll() {
        Long schoolId = TenantContext.getSchoolId();
        return assessmentRepository.findAllBySchoolIdOrderByDateDesc(schoolId).stream()
                .map(AssessmentDto::new)
                .toList();
    }

    public List<AssessmentDto> listMine(User teacher) {
        Long schoolId = TenantContext.getSchoolId();
        return assessmentRepository
                .findAllByAssignment_Teacher_IdAndSchoolIdOrderByDateDesc(teacher.getId(), schoolId)
                .stream()
                .map(AssessmentDto::new)
                .toList();
    }

    public List<AssessmentDto> listByTerm(Long termId) {
        Long schoolId = TenantContext.getSchoolId();
        return assessmentRepository.findAllByTermIdAndSchoolIdOrderByDateDesc(termId, schoolId).stream()
                .map(AssessmentDto::new)
                .toList();
    }

    public List<AssessmentDto> listByAssignment(Long assignmentId) {
        Long schoolId = TenantContext.getSchoolId();
        return assessmentRepository.findAllByAssignmentIdAndSchoolIdOrderByDateDesc(assignmentId, schoolId).stream()
                .map(AssessmentDto::new)
                .toList();
    }

    public AssessmentDto get(Long id) {
        return new AssessmentDto(findOrThrow(id));
    }

    @Transactional
    public AssessmentDto update(User editor, Long id, AssessmentDto dto) {
        Long schoolId = TenantContext.getSchoolId();
        Assessment assessment = findOrThrow(id);

        requireAccess(editor, assessment);

        if (dto.getTermId() != null) {
            Term term = termRepository.findById(dto.getTermId())
                    .orElseThrow(() -> new AssessmentExceptions.InvalidAssessmentException("Term not found"));

            if (!term.getAcademicYear().getId().equals(assessment.getAssignment().getAcademicYear().getId())) {
                throw new AssessmentExceptions.InvalidAssessmentException(
                        "Term and assignment belong to different academic years");
            }

            assessment.setTerm(term);
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            assessment.setName(dto.getName().trim());
        }
        if (dto.getMaxMark() != null) {
            Double highest = markRepository.findMaxScoreByAssessmentId(id);
            if (highest != null && dto.getMaxMark() < highest) {
                throw new AssessmentExceptions.InvalidAssessmentException(
                        "Max mark cannot be lower than the highest existing mark (" + highest + ")");
            }
            assessment.setMaxMark(dto.getMaxMark());
        }
        if (dto.getWeight() != null) {
            assessment.setWeight(dto.getWeight());
        }
        if (dto.getDate() != null) {
            assessment.setDate(dto.getDate());
        }

        assessment.setUpdatedBy(editor);

        validate(assessment);

        if (assessment.getType() == AssessmentType.EXAM && dto.getWeight() != null) {
            int otherWeight = assessmentRepository
                    .findAllByAssignment_SchoolClass_IdAndAssignment_Subject_IdAndTermIdAndSchoolId(
                            assessment.getAssignment().getSchoolClass().getId(),
                            assessment.getAssignment().getSubject().getId(),
                            assessment.getTerm().getId(),
                            schoolId)
                    .stream()
                    .filter(a -> a.getType() == AssessmentType.EXAM && !a.getId().equals(id))
                    .mapToInt(Assessment::getWeight)
                    .sum();

            if (otherWeight + dto.getWeight() > 100) {
                throw new AssessmentExceptions.InvalidAssessmentException(
                        "Exam weights for this subject must not exceed 100.");
            }
        }

        Assessment saved = assessmentRepository.save(assessment);
        return new AssessmentDto(saved);
    }

    @Transactional
    public void delete(User editor, Long id) {
        Assessment assessment = findOrThrow(id);
        requireAccess(editor, assessment);
        assessmentRepository.delete(assessment);
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

    private void validate(Assessment assessment) {
        if (assessment.getMaxMark() == null || assessment.getMaxMark() <= 0) {
            throw new AssessmentExceptions.InvalidAssessmentException("Max mark must be greater than zero");
        }
        if (assessment.getWeight() == null
                || assessment.getWeight() < 0
                || assessment.getWeight() > 100) {
            throw new AssessmentExceptions.InvalidAssessmentException("Weight must be between 0 and 100");
        }
    }

    private void validate(AssessmentDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new AssessmentExceptions.InvalidAssessmentException("Name is required");
        }
        if (dto.getMaxMark() == null || dto.getMaxMark() <= 0) {
            throw new AssessmentExceptions.InvalidAssessmentException("Max mark must be greater than zero");
        }
        if (dto.getWeight() == null || dto.getWeight() < 0 || dto.getWeight() > 100) {
            throw new AssessmentExceptions.InvalidAssessmentException("Weight must be between 0 and 100");
        }
        if (dto.getDate() == null) {
            throw new AssessmentExceptions.InvalidAssessmentException("Date is required");
        }
    }

    private Assessment findOrThrow(Long id) {
        Long schoolId = TenantContext.getSchoolId();
        return assessmentRepository.findByIdAndSchoolId(id, schoolId)
                .orElseThrow(() -> new AssessmentExceptions.AssessmentNotFoundException("Assessment not found"));
    }
}