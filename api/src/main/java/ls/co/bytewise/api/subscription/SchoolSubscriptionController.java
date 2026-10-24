package ls.co.bytewise.api.subscription;

import ls.co.bytewise.api.security.TenantContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/school/subscription-status")
public class SchoolSubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;

    public SchoolSubscriptionController(SubscriptionService subscriptionService,
                                        SubscriptionRepository subscriptionRepository) {
        this.subscriptionService = subscriptionService;
        this.subscriptionRepository = subscriptionRepository;
    }

    @GetMapping
    public SchoolSubscriptionStatusDto current() {
        Long schoolId = TenantContext.getSchoolId();
        return subscriptionRepository.findBySchoolId(schoolId)
                .map(sub -> {
                    SchoolSubscriptionStatusDto dto = new SchoolSubscriptionStatusDto();
                    dto.setStatus(subscriptionService.computeEffectiveStatus(sub));
                    dto.setPlan(sub.getPlan());
                    dto.setEndsAt(sub.getEndsAt());

                    long days = sub.getEndsAt() == null
                            ? Long.MAX_VALUE
                            : ChronoUnit.DAYS.between(Instant.now(), sub.getEndsAt());
                    dto.setDaysRemaining(Math.max(days, 0));
                    dto.setNearExpiry(days >= 0 && days <= 7);
                    dto.setBlocked(
                            dto.getStatus() == SubscriptionStatus.EXPIRED
                                    || dto.getStatus() == SubscriptionStatus.SUSPENDED
                                    || dto.getStatus() == SubscriptionStatus.CANCELLED
                    );
                    return dto;
                })
                .orElseGet(() -> {
                    SchoolSubscriptionStatusDto dto = new SchoolSubscriptionStatusDto();
                    dto.setStatus(SubscriptionStatus.EXPIRED);
                    dto.setBlocked(true);
                    return dto;
                });
    }
}