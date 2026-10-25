package ls.co.bytewise.api.result;

import ls.co.bytewise.api.audit.AuditAction;
import ls.co.bytewise.api.audit.AuditLogService;
import ls.co.bytewise.api.assessment.Assessment;
import ls.co.bytewise.api.assessment.AssessmentRepository;
import ls.co.bytewise.api.assessment.AssessmentType;
import ls.co.bytewise.api.assessment.mark.Mark;
import ls.co.bytewise.api.assessment.mark.MarkRepository;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.grading.GradeBand;
import ls.co.bytewise.api.grading.GradeBandRepository;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.student.StudentStatus;
import ls.co.bytewise.api.subject.Subject;
import ls.co.bytewise.api.subject.SubjectRepository;
import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.term.TermRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class TermResultService {

    private final TermResultRepository termResultRepository;
    private final ResultAuditRepository resultAuditRepository;
    private final AssessmentRepository assessmentRepository;
    private final MarkRepository markRepository;
    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SubjectRepository subjectRepository;
    private final TermRepository termRepository;
    private final GradeBandRepository gradeBandRepository;
    private final AuditLogService auditLogService;

    public TermResultService(TermResultRepository termResultRepository,
                             ResultAuditRepository resultAuditRepository,
                             AssessmentRepository assessmentRepository,
                             MarkRepository markRepository,
                             StudentRepository studentRepository,
                             SchoolClassRepository schoolClassRepository,
                             SubjectRepository subjectRepository,
                             TermRepository termRepository,
                             GradeBandRepository gradeBandRepository,
                             AuditLogService auditLogService) {
        this.termResultRepository = termResultRepository;
        this.resultAuditRepository = resultAuditRepository;
        this.assessmentRepository = assessmentRepository;
        this.markRepository = markRepository;
        this.studentRepository = studentRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.subjectRepository = subjectRepository;
        this.termRepository = termRepository;
        this.gradeBandRepository = gradeBandRepository;
        this.auditLogService = auditLogService;
    }

    public List<TermResultDto> listFor(Long classId, Long subjectId, Long termId) {
        Long schoolId = TenantContext.getSchoolId();
        return termResultRepository
                .findAllBySchoolClassIdAndSubjectIdAndTermIdAndSchoolId(classId, subjectId, termId, schoolId)
                .stream()
                .map(TermResultDto::new)
                .toList();
    }

    public List<TermResultDto> listForClassAndTerm(Long classId, Long termId) {
        Long schoolId = TenantContext.getSchoolId();
        return termResultRepository
                .findAllBySchoolClassIdAndTermIdAndSchoolId(classId, termId, schoolId)
                .stream()
                .map(TermResultDto::new)
                .toList();
    }

    public List<TermResultDto> listForStudentAndTerm(Long studentId, Long termId) {
        Long schoolId = TenantContext.getSchoolId();
        return termResultRepository
                .findAllByStudentIdAndTermIdAndSchoolId(studentId, termId, schoolId)
                .stream()
                .map(TermResultDto::new)
                .toList();
    }

    public List<ResultAuditDto> listAudit(Long termResultId) {
        Long schoolId = TenantContext.getSchoolId();

        TermResult result = termResultRepository.findById(termResultId)
                .orElseThrow(() -> new TermResultExceptions.TermResultNotFoundException("Result not found"));

        if (!result.getSchool().getId().equals(schoolId)) {
            throw new TermResultExceptions.TermResultNotFoundException("Result not found");
        }

        return resultAuditRepository.findAllByTermResultIdOrderByCreatedAtDesc(termResultId).stream()
                .map(ResultAuditDto::new)
                .toList();
    }

    @Transactional
    public List<TermResultDto> calculate(User currentUser, Long classId, Long subjectId, Long termId) {
        Long schoolId = TenantContext.getSchoolId();

        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new TermResultExceptions.InvalidTermResultOperationException("Class not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new TermResultExceptions.InvalidTermResultOperationException("Subject not found"));

        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new TermResultExceptions.InvalidTermResultOperationException("Term not found"));

        if (!schoolClass.getSchool().getId().equals(schoolId)
                || !subject.getSchool().getId().equals(schoolId)
                || !term.getSchool().getId().equals(schoolId)) {
            throw new TermResultExceptions.InvalidTermResultOperationException("Entities do not belong to the active school");
        }

        List<Assessment> exams = assessmentRepository
                .findAllByAssignment_SchoolClass_IdAndAssignment_Subject_IdAndTermIdAndSchoolId(
                        classId, subjectId, termId, schoolId)
                .stream()
                .filter(a -> a.getType() == AssessmentType.EXAM)
                .toList();

        if (exams.isEmpty()) {
            throw new TermResultExceptions.InvalidTermResultOperationException(
                    "No exam assessments exist for this subject and class in this term");
        }

        int totalWeight = exams.stream().mapToInt(Assessment::getWeight).sum();
        if (totalWeight != 100) {
            throw new TermResultExceptions.InvalidTermResultOperationException(
                    "Exam weights for this subject must sum to 100. Currently: " + totalWeight);
        }

        List<GradeBand> bands = gradeBandRepository
                .findAllBySchoolIdOrderByMinScoreDesc(schoolId);

        List<Student> students = studentRepository
                .findAllBySchoolIdAndCurrentClassId(schoolId, classId)
                .stream()
                .filter(s -> s.getStatus() == StudentStatus.ACTIVE)
                .toList();

        for (Student student : students) {
            TermResult result = termResultRepository
                    .findByStudentIdAndSubjectIdAndTermIdAndSchoolId(
                            student.getId(), subjectId, termId, schoolId)
                    .orElseGet(TermResult::new);

            if (result.getStatus() == TermResultStatus.FINALISED) {
                continue;
            }

            double finalMark = 0.0;
            for (Assessment exam : exams) {
                Mark mark = markRepository
                        .findByAssessmentIdAndStudentId(exam.getId(), student.getId())
                        .orElse(null);

                double score = (mark != null && mark.getScore() != null) ? mark.getScore() : 0.0;
                double ratio = score / exam.getMaxMark();
                finalMark += ratio * exam.getWeight();
            }

            result.setSchool(schoolClass.getSchool());
            result.setStudent(student);
            result.setSubject(subject);
            result.setSchoolClass(schoolClass);
            result.setTerm(term);
            result.setFinalMark(round(finalMark));
            result.setStatus(TermResultStatus.CALCULATED);
            result.setCalculatedAt(Instant.now());

            if (result.getId() == null) {
                result.setCreatedBy(currentUser);
            }
            result.setUpdatedBy(currentUser);

            applyGrade(result, bands);

            termResultRepository.save(result);
        }

        return listFor(classId, subjectId, termId);
    }

    @Transactional
    public List<TermResultDto> finalise(User currentUser, Long classId, Long subjectId, Long termId) {
        Long schoolId = TenantContext.getSchoolId();

        List<TermResult> results = termResultRepository
                .findAllBySchoolClassIdAndSubjectIdAndTermIdAndSchoolId(classId, subjectId, termId, schoolId);

        if (results.isEmpty()) {
            throw new TermResultExceptions.InvalidTermResultOperationException(
                    "No results to finalise. Calculate first.");
        }

        Instant now = Instant.now();
        for (TermResult result : results) {
            if (result.getStatus() == TermResultStatus.FINALISED) continue;
            result.setStatus(TermResultStatus.FINALISED);
            result.setFinalisedAt(now);
            result.setFinalisedBy(currentUser);
            result.setUpdatedBy(currentUser);
            termResultRepository.save(result);
        }

        return listFor(classId, subjectId, termId);
    }

    @Transactional
    public List<TermResultDto> reopen(User currentUser, Long classId, Long subjectId, Long termId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new TermResultExceptions.InvalidTermResultOperationException(
                    "A reason is required to reopen finalised results");
        }

        Long schoolId = TenantContext.getSchoolId();

        List<TermResult> results = termResultRepository
                .findAllBySchoolClassIdAndSubjectIdAndTermIdAndSchoolId(classId, subjectId, termId, schoolId);

        Instant now = Instant.now();
        for (TermResult result : results) {
            if (result.getStatus() != TermResultStatus.FINALISED) continue;

            ResultAudit audit = new ResultAudit();
            audit.setSchool(result.getSchool());
            audit.setTermResult(result);
            audit.setPreviousMark(result.getFinalMark());
            audit.setNewMark(null);
            audit.setReason(reason.trim());
            audit.setChangedBy(currentUser);
            audit.setCreatedBy(currentUser);
            audit.setUpdatedBy(currentUser);
            resultAuditRepository.save(audit);

            result.setStatus(TermResultStatus.CALCULATED);
            result.setFinalisedAt(null);
            result.setFinalisedBy(null);
            result.setUpdatedBy(currentUser);
            termResultRepository.save(result);
            auditLogService.record(result.getSchool(), currentUser,
                    AuditAction.RESULT_CHANGED_AFTER_FINALISATION,
                    "TERM_RESULT", result.getId(),
                    "Reopened finalised result for " + result.getStudent().getUser().getFullName()
                            + " in " + result.getSubject().getName() + "; reason: " + reason.trim());
        }

        return listFor(classId, subjectId, termId);
    }

    @Transactional
    public TermResultDto overrideMark(User currentUser, Long resultId, Double newMark, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new TermResultExceptions.InvalidTermResultOperationException(
                    "A reason is required to override a finalised result");
        }

        Long schoolId = TenantContext.getSchoolId();

        TermResult result = termResultRepository.findById(resultId)
                .orElseThrow(() -> new TermResultExceptions.TermResultNotFoundException("Result not found"));

        if (!result.getSchool().getId().equals(schoolId)) {
            throw new TermResultExceptions.TermResultNotFoundException("Result not found");
        }

        if (result.getStatus() != TermResultStatus.FINALISED) {
            throw new TermResultExceptions.InvalidTermResultOperationException(
                    "Only finalised results can be overridden");
        }

        if (newMark == null || newMark < 0 || newMark > 100) {
            throw new TermResultExceptions.InvalidTermResultOperationException(
                    "Mark must be between 0 and 100");
        }

        double previousMark = result.getFinalMark();
        ResultAudit audit = new ResultAudit();
        audit.setSchool(result.getSchool());
        audit.setTermResult(result);
        audit.setPreviousMark(previousMark);
        audit.setNewMark(newMark);
        audit.setReason(reason.trim());
        audit.setChangedBy(currentUser);
        audit.setCreatedBy(currentUser);
        audit.setUpdatedBy(currentUser);
        resultAuditRepository.save(audit);

        result.setFinalMark(newMark);
        result.setUpdatedBy(currentUser);

        List<GradeBand> bands = gradeBandRepository
                .findAllBySchoolIdOrderByMinScoreDesc(schoolId);
        applyGrade(result, bands);

        termResultRepository.save(result);
        auditLogService.record(result.getSchool(), currentUser,
                AuditAction.RESULT_CHANGED_AFTER_FINALISATION,
                "TERM_RESULT", result.getId(),
                "Changed finalised result for " + result.getStudent().getUser().getFullName()
                        + " in " + result.getSubject().getName() + " from "
                        + previousMark + " to " + newMark + "; reason: " + reason.trim());
        return new TermResultDto(result);
    }

    private void applyGrade(TermResult result, List<GradeBand> bands) {
        for (GradeBand band : bands) {
            if (result.getFinalMark() >= band.getMinScore() && result.getFinalMark() <= band.getMaxScore()) {
                result.setGrade(band.getGrade());
                result.setRemark(band.getRemark());
                return;
            }
        }
        result.setGrade(null);
        result.setRemark(null);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public StudentResultsReportDto myResults(User user, Long termId) {
        Long schoolId = TenantContext.getSchoolId();

        Student student = studentRepository.findByUserId(user.getId())
                .filter(s -> s.getSchool().getId().equals(schoolId))
                .orElseThrow(() -> new TermResultExceptions.TermResultNotFoundException(
                        "No student record linked to this account"));

        Term term = termRepository.findByIdAndSchoolId(termId, schoolId)
                .orElseThrow(() -> new TermResultExceptions.TermResultNotFoundException("Term not found"));

        List<TermResult> visible = termResultRepository
                .findAllByStudentIdAndTermIdAndSchoolId(student.getId(), termId, schoolId)
                .stream()
                .filter(r -> r.getStatus() == TermResultStatus.FINALISED)
                .toList();

        StudentResultsReportDto report = new StudentResultsReportDto();
        report.setStudentId(student.getId());
        report.setFirstName(student.getUser().getFirstName());
        report.setLastName(student.getUser().getLastName());
        report.setAdmissionNumber(student.getAdmissionNumber());
        report.setTermId(term.getId());
        report.setTermName(term.getName());
        report.setSubjects(visible.stream().map(TermResultDto::new).toList());

        if (!visible.isEmpty()) {
            double avg = visible.stream()
                    .mapToDouble(TermResult::getFinalMark)
                    .average()
                    .orElse(0.0);
            double rounded = round(avg);
            report.setAverageMark(rounded);

            List<GradeBand> bands = gradeBandRepository
                    .findAllBySchoolIdOrderByMinScoreDesc(schoolId);
            for (GradeBand band : bands) {
                if (rounded >= band.getMinScore() && rounded <= band.getMaxScore()) {
                    report.setOverallGrade(band.getGrade());
                    report.setOverallRemark(band.getRemark());
                    break;
                }
            }
        }

        return report;
    }
}
