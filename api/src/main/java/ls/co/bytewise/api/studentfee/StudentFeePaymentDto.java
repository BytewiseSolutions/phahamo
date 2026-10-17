package ls.co.bytewise.api.studentfee;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ls.co.bytewise.api.payment.PaymentMethodType;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class StudentFeePaymentDto {

    private Long id;
    private Double amount;
    private PaymentMethodType method;
    private String reference;
    private Instant paidAt;
    private Double balanceAfter;
    private Long receivedById;
    private String receivedByName;

    public StudentFeePaymentDto(StudentFeePayment p) {
        this.id = p.getId();
        this.amount = p.getAmount();
        this.method = p.getMethod();
        this.balanceAfter = p.getBalanceAfter();
        this.reference = p.getReference();
        this.paidAt = p.getPaidAt();
        if (p.getReceivedBy() != null) {
            this.receivedById = p.getReceivedBy().getId();
            this.receivedByName = p.getReceivedBy().getFullName();
        }
    }
}