package ls.co.bytewise.api.school.subscription.plan;

import ls.co.bytewise.api.school.Currency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ls.co.bytewise.api.school.subscription.SubscriptionPlan;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PlanPriceDto {

    private Long id;
    private SubscriptionPlan plan;
    private BigDecimal amount;
    private Currency currency;
    private Boolean active;

    public PlanPriceDto(PlanPrice price) {
        this.id = price.getId();
        this.plan = price.getPlan();
        this.amount = price.getAmount();
        this.currency = price.getCurrency();
        this.active = price.isActive();
    }
}