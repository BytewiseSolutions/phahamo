package ls.co.bytewise.api.studentfee;

import ls.co.bytewise.api.mail.MailService;
import ls.co.bytewise.api.parent.Parent;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class FeeStatementService {

    private final StudentFeeAccountRepository accountRepository;
    private final MailService mailService;

    public FeeStatementService(StudentFeeAccountRepository accountRepository,
                               MailService mailService) {
        this.accountRepository = accountRepository;
        this.mailService = mailService;
    }

    @Transactional
    public FeeStatementResultDto emailForAccount(Long accountId) {
        Long schoolId = TenantContext.getSchoolId();
        StudentFeeAccount account = accountRepository.findByIdAndSchoolId(accountId, schoolId)
                .orElseThrow(() -> new StudentFeeExceptions.AccountNotFoundException("Account not found"));

        FeeStatementResultDto result = new FeeStatementResultDto();
        sendOne(account, result);
        return result;
    }

    @Transactional
    public FeeStatementResultDto emailAllOutstanding(Long termId) {
        Long schoolId = TenantContext.getSchoolId();

        List<StudentFeeAccount> accounts = accountRepository
                .findAllByTermIdAndSchoolId(termId, schoolId)
                .stream()
                .filter(a -> a.getBalance() != null && a.getBalance() > 0)
                .toList();

        FeeStatementResultDto result = new FeeStatementResultDto();
        for (StudentFeeAccount acc : accounts) {
            sendOne(acc, result);
        }
        return result;
    }

    private void sendOne(StudentFeeAccount account, FeeStatementResultDto result) {
        Student student = account.getStudent();
        School school = account.getSchool();
        String displayName = student.getUser().getFullName()
                + " (" + student.getAdmissionNumber() + ")";

        if (account.getBalance() == null || account.getBalance() <= 0) {
            result.setSkippedPaidCount(result.getSkippedPaidCount() + 1);
            result.getSkippedPaid().add(displayName + " — balance is 0");
            return;
        }

        List<Parent> parentsWithEmail = student.getParents().stream()
                .filter(p -> p.getUser() != null
                        && p.getUser().getEmail() != null
                        && !p.getUser().getEmail().isBlank())
                .toList();

        if (parentsWithEmail.isEmpty()) {
            result.setSkippedNoEmailCount(result.getSkippedNoEmailCount() + 1);
            result.getSkippedNoEmail().add(displayName + " — no parent email");
            return;
        }

        byte[] pdf;
        try {
            StudentFeeAccountDto accountDto = new StudentFeeAccountDto(account);
            pdf = FeeStatementRenderer.render(accountDto, school, LocalDate.now());
        } catch (Exception e) {
            result.setFailedCount(result.getFailedCount() + 1);
            result.getFailed().add(displayName + " — PDF render failed: " + e.getMessage());
            return;
        }

        String filename = "fee-statement-"
                + student.getAdmissionNumber() + "-"
                + account.getTerm().getName().replaceAll("\\s+", "-") + ".pdf";

        String subject = "Fee Statement — " + student.getUser().getFullName();
        String body = buildEmailBody(account);

        boolean anySent = false;
        for (Parent parent : parentsWithEmail) {
            try {
                mailService.sendWithAttachmentBlocking(
                        parent.getUser().getEmail(),
                        subject,
                        body,
                        filename,
                        pdf,
                        "application/pdf"
                );
                result.setSentCount(result.getSentCount() + 1);
                anySent = true;
            } catch (Exception e) {
                result.setFailedCount(result.getFailedCount() + 1);
                result.getFailed().add(
                        parent.getUser().getEmail() + " (" + displayName + ") — " + e.getMessage());
            }
        }

        if (anySent) {
            account.setLastStatementSentAt(Instant.now());
            accountRepository.save(account);
        }
    }

    private String buildEmailBody(StudentFeeAccount account) {
        Student student = account.getStudent();
        String currency = account.getSchool().getCurrency() != null
                ? account.getSchool().getCurrency().name()
                : "M";
        String balance = String.format("%.2f", account.getBalance());

        return "Dear Parent,\n\n" +
                "Please find attached the fee statement for " +
                student.getUser().getFullName() + " for " +
                account.getTerm().getName() + ".\n\n" +
                "Outstanding balance: " + currency + " " + balance + "\n\n" +
                "Regards,\n" + account.getSchool().getName();
    }
}