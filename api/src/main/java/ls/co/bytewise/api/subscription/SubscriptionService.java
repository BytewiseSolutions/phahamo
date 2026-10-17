package ls.co.bytewise.api.subscription;

import ls.co.bytewise.api.school.SchoolExceptions;
import ls.co.bytewise.api.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    public List<SubscriptionDto> listAll() {
        return subscriptionRepository.findAllByOrderByEndsAtAsc().stream()
                .map(SubscriptionDto::new)
                .toList();
    }

    @Transactional
    public SubscriptionDto extend(Long schoolId, int months) {
        if (months <= 0) {
            throw new IllegalArgumentException("Months must be greater than zero");
        }

        Subscription subscription = findBySchoolIdOrThrow(schoolId);

        Instant now = Instant.now();
        Instant base = subscription.getEndsAt() == null || subscription.getEndsAt().isBefore(now)
                ? now
                : subscription.getEndsAt();

        subscription.setEndsAt(base.plus(months * 30L, ChronoUnit.DAYS));

        if (subscription.getStatus() == SubscriptionStatus.EXPIRED) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }

        Subscription saved = subscriptionRepository.save(subscription);
        return new SubscriptionDto(saved);
    }

    @Transactional
    public SubscriptionDto changePlan(Long schoolId, SubscriptionPlan plan, int months) {
        Subscription subscription = findBySchoolIdOrThrow(schoolId);

        subscription.setPlan(plan);

        if (plan == SubscriptionPlan.ONE_OFF) {
            subscription.setEndsAt(null);
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        } else {
            Instant now = Instant.now();
            subscription.setStartedAt(now);
            subscription.setEndsAt(now.plus(months * 30L, ChronoUnit.DAYS));
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }

        Subscription saved = subscriptionRepository.save(subscription);
        return new SubscriptionDto(saved);
    }

    @Transactional
    public SubscriptionDto cancel(Long schoolId) {
        Subscription subscription = findBySchoolIdOrThrow(schoolId);
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        Subscription saved = subscriptionRepository.save(subscription);
        return new SubscriptionDto(saved);
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

    private Subscription findBySchoolIdOrThrow(Long schoolId) {
        return subscriptionRepository.findBySchoolId(schoolId)
                .orElseThrow(() -> new SchoolExceptions.SchoolNotFoundException(
                        "No subscription found for this school"));
    }
}