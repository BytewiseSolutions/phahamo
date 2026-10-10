package ls.co.bytewise.api.school.subscription;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
public class SubscriptionDto {

    private Long id;
    private Long schoolId;
    private String schoolName;
    private SubscriptionPlan plan;
    private SubscriptionStatus status;
    private Instant startedAt;
    private Instant endsAt;
    private long daysRemaining;

    public SubscriptionDto(Subscription subscription) {
        this.id = subscription.getId();
        this.schoolId = subscription.getSchool().getId();
        this.schoolName = subscription.getSchool().getName();
        this.plan = subscription.getPlan();
        this.status = subscription.getStatus();
        this.startedAt = subscription.getStartedAt();
        this.endsAt = subscription.getEndsAt();

        if (this.endsAt == null) {
            this.daysRemaining = -1;
        } else {
            long days = ChronoUnit.DAYS.between(Instant.now(), this.endsAt);
            this.daysRemaining = days < 0 ? 0 : days;
        }
    }
}