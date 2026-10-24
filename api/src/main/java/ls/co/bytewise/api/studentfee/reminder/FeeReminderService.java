package ls.co.bytewise.api.studentfee.reminder;

import ls.co.bytewise.api.mail.MailService;
import ls.co.bytewise.api.parent.Parent;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.studentfee.*;
import ls.co.bytewise.api.term.Term;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeeReminderService {

    private final StudentFeeAccountRepository accountRepository;
    private final MailService mailService;

    public FeeReminderService(StudentFeeAccountRepository accountRepository,
                              MailService mailService) {
        this.accountRepository = accountRepository;
        this.mailService = mailService;
    }

    public List<OutstandingStudentDto> getOutstanding(Long termId) {
        Long schoolId = TenantContext.getSchoolId();

        List<StudentFeeAccount> accounts = accountRepository
                .findAllByTermIdAndSchoolId(termId, schoolId);

        List<OutstandingStudentDto> result = new ArrayList<>();
        for (StudentFeeAccount acc : accounts) {
            if (acc.getBalance() == null || acc.getBalance() <= 0) continue;

            Student student = acc.getStudent();
            if (student == null) continue;

            OutstandingStudentDto dto = new OutstandingStudentDto();
            dto.setAccountId(acc.getId());
            dto.setStudentId(student.getId());
            dto.setStudentFullName(student.getUser().getFullName());
            dto.setAdmissionNumber(student.getAdmissionNumber());
            dto.setClassName(acc.getSchoolClass().getName());
            dto.setBalance(acc.getBalance());
            dto.setLastReminderSentAt(acc.getLastReminderSentAt());
            dto.setLastReminderAmount(acc.getLastReminderAmount());

            List<ParentContactDto> parents = new ArrayList<>();
            boolean hasEmailParent = false;
            for (Parent p : student.getParents()) {
                if (p.getUser() == null) continue;
                String email = p.getUser().getEmail();
                boolean hasEmail = email != null && !email.isBlank();
                if (hasEmail) hasEmailParent = true;
                parents.add(new ParentContactDto(
                        p.getUser().getFullName(),
                        email,
                        p.getUser().getPhone(),
                        hasEmail
                ));
            }
            dto.setParents(parents);
            dto.setHasEmailParent(hasEmailParent);

            result.add(dto);
        }

        result.sort((a, b) -> a.getAdmissionNumber().compareTo(b.getAdmissionNumber()));
        return result;
    }

    @Transactional
    public BulkFeeEmailResultDto sendToAll(Long termId) {
        Long schoolId = TenantContext.getSchoolId();

        List<StudentFeeAccount> accounts = accountRepository
                .findAllByTermIdAndSchoolId(termId, schoolId)
                .stream()
                .filter(a -> a.getBalance() != null && a.getBalance() > 0)
                .toList();

        return sendToAccounts(accounts);
    }

    @Transactional
    public BulkFeeEmailResultDto sendToSelected(List<Long> accountIds) {
        Long schoolId = TenantContext.getSchoolId();

        if (accountIds == null || accountIds.isEmpty()) {
            throw new StudentFeeExceptions.InvalidFeeOperationException(
                    "No accounts selected");
        }

        List<StudentFeeAccount> accounts = new ArrayList<>();
        for (Long id : accountIds) {
            accountRepository.findByIdAndSchoolId(id, schoolId)
                    .ifPresent(accounts::add);
        }

        return sendToAccounts(accounts);
    }

    private BulkFeeEmailResultDto sendToAccounts(List<StudentFeeAccount> accounts) {
        BulkFeeEmailResultDto result = new BulkFeeEmailResultDto();
        Instant now = Instant.now();

        for (StudentFeeAccount acc : accounts) {
            Student student = acc.getStudent();
            if (acc.getBalance() == null || acc.getBalance() <= 0) {
                result.setSkippedPaidCount(result.getSkippedPaidCount() + 1);
                continue;
            }

            List<Parent> parentsWithEmail = acc.getStudent().getParents().stream()
                    .filter(p -> p.getUser() != null
                            && p.getUser().getEmail() != null
                            && !p.getUser().getEmail().isBlank())
                    .toList();

            if (parentsWithEmail.isEmpty()) {
                result.setSkippedNoEmailCount(result.getSkippedNoEmailCount() + 1);
                continue;
            }

            String subject = "Fee Reminder — " + student.getUser().getFullName();
            String body = buildReminderBody(acc);

            boolean anySent = false;
            for (Parent parent : parentsWithEmail) {
                try {
                    mailService.send(parent.getUser().getEmail(), subject, body);
                    result.setSentCount(result.getSentCount() + 1);
                    anySent = true;
                } catch (Exception e) {
                    result.setFailedCount(result.getFailedCount() + 1);
                }
            }

            if (anySent) {
                acc.setLastReminderSentAt(now);
                acc.setLastReminderAmount(acc.getBalance());
                accountRepository.save(acc);
            }
        }

        return result;
    }

    private String buildReminderBody(StudentFeeAccount acc) {
        Student student = acc.getStudent();
        Term term = acc.getTerm();
        String currency = acc.getSchool().getCurrency() != null
                ? acc.getSchool().getCurrency().name()
                : "M";
        String balance = String.format("%.2f", acc.getBalance());

        return "Dear Parent,\n\n" +
                "This is a reminder that " + student.getUser().getFullName() +
                " has an outstanding balance of " + currency + " " + balance +
                " for " + term.getName() + ".\n\n" +
                "Please arrange payment at your earliest convenience.\n\n" +
                "If you have already paid, please disregard this message.\n\n" +
                "Regards,\n" +
                acc.getSchool().getName();
    }
}
