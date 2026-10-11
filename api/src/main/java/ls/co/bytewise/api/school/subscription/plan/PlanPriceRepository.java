package ls.co.bytewise.api.school.subscription.plan;

import ls.co.bytewise.api.school.Currency;
import ls.co.bytewise.api.school.subscription.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanPriceRepository extends JpaRepository<PlanPrice, Long> {

    Optional<PlanPrice> findByPlanAndCurrency(SubscriptionPlan plan, Currency currency);

    List<PlanPrice> findAllByCurrencyOrderByPlanAsc(Currency currency);

    List<PlanPrice> findAllByOrderByCurrencyAscPlanAsc();
}