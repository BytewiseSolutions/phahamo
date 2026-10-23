package ls.co.bytewise.api.reportcard.email;

import ls.co.bytewise.api.mail.MailService;
import ls.co.bytewise.api.parent.Parent;
import ls.co.bytewise.api.reportcard.ReportCardDto;
import ls.co.bytewise.api.reportcard.ReportCardRenderer;
import ls.co.bytewise.api.reportcard.ReportCardService;
import ls.co.bytewise.api.result.TermResult;
import ls.co.bytewise.api.result.TermResultRepository;
import ls.co.bytewise.api.result.TermResultStatus;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.term.TermRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReportCardEmailService {

    private static final String SUBJECT_LINE = "Report Card";
    private static final String MIME_PDF = "application/pdf";

    private final ReportCardService reportCardService;
    private final ReportCardRenderer reportCardRenderer;
    private final TermResultRepository termResultRepository;
    private final TermRepository termRepository;
    private final StudentRepository studentRepository;
    private final MailService mailService;

    public ReportCardEmailService(ReportCardService reportCardService,
                                  ReportCardRenderer reportCardRenderer,
                                  TermResultRepository termResultRepository,
                                  TermRepository termRepository,
                                  StudentRepository studentRepository,
                                  MailService mailService) {
        this.reportCardService = reportCardService;
        this.reportCardRenderer = reportCardRenderer;
        this.termResultRepository = termResultRepository;
        this.termRepository = termRepository;
        this.studentRepository = studentRepository;
        this.mailService = mailService;
    }

    @Transactional(readOnly = true)
    public EmailReportCardResultDto emailForStudent(Long studentId, Long termId, User sender) {
        Long schoolId = TenantContext.getSchoolId();

        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Term term = termRepository.findByIdAndSchoolId(termId, schoolId)
                .orElseThrow(() -> new IllegalArgumentException("Term not found"));

        EmailReportCardResultDto result = new EmailReportCardResultDto();

        sendOne(student, term, sender, result);
        return result;
    }

    @Transactional(readOnly = true)
    public EmailReportCardResultDto emailForClass(Long classId, Long termId, User sender) {
        Long schoolId = TenantContext.getSchoolId();

        SchoolClass schoolClass = reportCardService.requireClass(classId);
        Term term = termRepository.findByIdAndSchoolId(termId, schoolId)
                .orElseThrow(() -> new IllegalArgumentException("Term not found"));

        List<Student> students = studentRepository
                .findAllBySchoolIdAndCurrentClassId(schoolId, classId);

        EmailReportCardResultDto result = new EmailReportCardResultDto();
        for (Student s : students) {
            sendOne(s, term, sender, result);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<ParentWithoutEmailDto> parentsWithoutEmail(Long classId) {
        Long schoolId = TenantContext.getSchoolId();
        List<Student> students = studentRepository
                .findAllBySchoolIdAndCurrentClassId(schoolId, classId);

        return students.stream()
                .flatMap(s -> s.getParents().stream()
                        .filter(p -> p.getUser() == null
                                || p.getUser().getEmail() == null
                                || p.getUser().getEmail().isBlank())
                        .map(p -> new ParentWithoutEmailDto(
                                s.getId(),
                                s.getUser().getFullName(),
                                s.getAdmissionNumber(),
                                p.getUser() == null ? "" : p.getUser().getFullName())))
                .toList();
    }

    private void sendOne(Student student, Term term, User sender,
                         EmailReportCardResultDto result) {
        Long schoolId = TenantContext.getSchoolId();
        String displayName = student.getUser().getFullName()
                + " (" + student.getAdmissionNumber() + ")";

        List<TermResult> allResults = termResultRepository
                .findAllByStudentIdAndTermIdAndSchoolId(student.getId(), term.getId(), schoolId);

        long finalised = allResults.stream()
                .filter(r -> r.getStatus() == TermResultStatus.FINALISED)
                .count();

        if (allResults.isEmpty() || finalised != allResults.size()) {
            result.setSkippedNotFinalisedCount(result.getSkippedNotFinalisedCount() + 1);
            result.getSkippedNotFinalised().add(displayName);
            return;
        }

        byte[] pdf;
        try {
            ReportCardDto dto = reportCardService.buildForStudent(
                    student.getId(), term.getId(), sender);
            pdf = ReportCardRenderer.render(dto);
        } catch (Exception e) {
            result.setFailedCount(result.getFailedCount() + 1);
            result.getFailed().add(displayName + " — PDF render failed: " + e.getMessage());
            return;
        }

        String filename = "report-card-"
                + student.getAdmissionNumber() + "-"
                + term.getName().replaceAll("\\s+", "-") + ".pdf";

        String body = "Dear Parent,\n\n"
                + "Please find attached the report card for " + student.getUser().getFullName()
                + " for " + term.getName() + ".\n\n"
                + "Regards,\nSchool Administration";

        List<Parent> parents = student.getParents().stream().toList();
        if (parents.isEmpty()) {
            result.setSkippedNoEmailCount(result.getSkippedNoEmailCount() + 1);
            result.getSkippedNoEmail().add(displayName + " — no parent linked");
            return;
        }

        boolean anySent = false;
        for (Parent p : parents) {
            String email = p.getUser() == null ? null : p.getUser().getEmail();
            if (email == null || email.isBlank()) {
                continue;
            }
            try {
                mailService.sendWithAttachmentBlocking(
                        email,
                        SUBJECT_LINE + " — " + term.getName(),
                        body,
                        filename,
                        pdf,
                        MIME_PDF
                );
                result.setSentCount(result.getSentCount() + 1);
                anySent = true;
            } catch (Exception e) {
                result.setFailedCount(result.getFailedCount() + 1);
                result.getFailed().add(email + " (" + displayName + ") — " + e.getMessage());
            }
        }

        if (!anySent) {
            result.setSkippedNoEmailCount(result.getSkippedNoEmailCount() + 1);
            result.getSkippedNoEmail().add(displayName + " — all parents have no email");
        }
    }
}