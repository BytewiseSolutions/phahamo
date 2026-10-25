package ls.co.bytewise.api.studentfee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import ls.co.bytewise.api.payment.PaymentMethodType;
import ls.co.bytewise.api.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "student_fee_payment")
public class StudentFeePayment extends SchoolScopedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private StudentFeeAccount account;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethodType method;

    @Column(name = "balance_after", nullable = false)
    private Double balanceAfter = 0.0;

    @Column(length = 100)
    private String reference;

    @Column(nullable = false)
    private Instant paidAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentStatus status = PaymentStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by")
    private User receivedBy;

    @Column(name = "reversal_reason", length = 500)
    private String reversalReason;

    @Column(name = "reversed_at")
    private Instant reversedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reversed_by")
    private User reversedBy;
}
