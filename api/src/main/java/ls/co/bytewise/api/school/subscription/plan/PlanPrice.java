package ls.co.bytewise.api.school.subscription.plan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import ls.co.bytewise.api.common.BaseEntity;
import ls.co.bytewise.api.school.Currency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ls.co.bytewise.api.school.subscription.SubscriptionPlan;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "plan_price",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_plan_price_plan_currency",
                columnNames = {"plan", "currency"}
        )
)
public class PlanPrice extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPlan plan;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private boolean active = true;
}