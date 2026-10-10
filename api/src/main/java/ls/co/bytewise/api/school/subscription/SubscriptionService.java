package ls.co.bytewise.api.school.subscription;

import ls.co.bytewise.api.school.SchoolExceptions;
import ls.co.bytewise.api.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public SubscriptionDto getForSchool(Long schoolId) {
        return subscriptionRepository.findBySchoolId(schoolId)
                .map(SubscriptionDto::new)
                .orElseThrow(() -> new SchoolExceptions.SchoolNotFoundException(
                        "No subscription found for this school"));
    }

    public SubscriptionDto getForCurrentSchool() {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new IllegalStateException("No active school context");
        }
        return getForSchool(schoolId);
    }

    @Transactional
    public void runExpiryCheck() {
        Instant now = Instant.now();

        List<Subscription> toExpire = subscriptionRepository.findAllByStatusInAndEndsAtBefore(
                List.of(SubscriptionStatus.TRIAL, SubscriptionStatus.ACTIVE),
                now
        );

        for (Subscription sub : toExpire) {
            sub.setStatus(SubscriptionStatus.EXPIRED);
        }

        subscriptionRepository.saveAll(toExpire);
    }
}