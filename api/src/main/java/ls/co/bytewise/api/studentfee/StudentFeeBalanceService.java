package ls.co.bytewise.api.studentfee;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentFeeBalanceService {

    private final StudentFeeAccountRepository accountRepository;

    public StudentFeeBalanceService(StudentFeeAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public double currentTermBalance(StudentFeeAccount account) {
        double previousBalance = rawPreviousBalance(account);
        double paymentAppliedToCurrentTerm = Math.max(0.0,
                account.getAmountPaid() - previousBalance);
        return Math.max(0.0, account.getTotalFees()
                - account.getTotalAdjustments() - paymentAppliedToCurrentTerm);
    }

    public List<PreviousBalanceDto> previousBalances(StudentFeeAccount account) {
        Instant termStart = account.getTerm().getStartDate();
        double paymentRemaining = Math.max(0.0, account.getAmountPaid());
        List<PreviousBalanceDto> result = new ArrayList<>();
        for (StudentFeeAccount previous : accountRepository.findPriorAccounts(
                account.getStudent().getId(), account.getSchool().getId(), termStart)) {
            double outstanding = currentTermBalance(previous);
            double applied = Math.min(paymentRemaining, outstanding);
            paymentRemaining -= applied;
            double remaining = outstanding - applied;
            if (remaining > 0) {
                result.add(new PreviousBalanceDto(previous, remaining));
            }
        }
        return result;
    }

    public double previousBalance(StudentFeeAccount account) {
        return previousBalances(account).stream()
                .mapToDouble(PreviousBalanceDto::getAmount)
                .sum();
    }

    public double totalBalance(StudentFeeAccount account) {
        return currentTermBalance(account) + previousBalance(account);
    }

    private double rawPreviousBalance(StudentFeeAccount account) {
        Instant termStart = account.getTerm().getStartDate();
        return accountRepository.findPriorAccounts(
                        account.getStudent().getId(), account.getSchool().getId(), termStart)
                .stream()
                .mapToDouble(this::currentTermBalance)
                .sum();
    }
}
