package ls.co.bytewise.api.student;

import lombok.Getter;

@Getter
public class StudentClassFeePreviewLineDto {
    private final String termName;
    private final double oldTotalFees;
    private final double newTotalFees;
    private final double amountPaid;
    private final double oldBalance;
    private final double newBalance;
    private final boolean willUpdate;

    public StudentClassFeePreviewLineDto(String termName,
                                         double oldTotalFees,
                                         double newTotalFees,
                                         double amountPaid,
                                         double oldBalance,
                                         double newBalance,
                                         boolean willUpdate) {
        this.termName = termName;
        this.oldTotalFees = oldTotalFees;
        this.newTotalFees = newTotalFees;
        this.amountPaid = amountPaid;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
        this.willUpdate = willUpdate;
    }
}
