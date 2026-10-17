package ls.co.bytewise.api.subscription.request;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ls.co.bytewise.api.common.BaseEntity;
import ls.co.bytewise.api.common.DataFile;
import ls.co.bytewise.api.school.Currency;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.subscription.SubscriptionPlan;
import ls.co.bytewise.api.payment.PaymentMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "subscription_request")
public class SubscriptionRequest extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPlan plan;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod method;

    @ManyToOne
    @JoinColumn(name = "proof_file_id")
    private DataFile proofFile;

    @Column(length = 2000)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionRequestStatus status = SubscriptionRequestStatus.PENDING;

    @Column(length = 2000)
    private String reviewNote;
}