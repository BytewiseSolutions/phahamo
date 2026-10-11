package ls.co.bytewise.api.school.subscription.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import ls.co.bytewise.api.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payment_method")
public class PaymentMethod extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethodType type;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false, length = 2000)
    private String details;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private int sortOrder = 0;
}