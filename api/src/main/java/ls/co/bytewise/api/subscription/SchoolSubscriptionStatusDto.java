package ls.co.bytewise.api.subscription;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class SchoolSubscriptionStatusDto {
    private SubscriptionStatus status;
    private SubscriptionPlan plan;
    private Instant endsAt;
    private long daysRemaining;
    private boolean nearExpiry;
    private boolean blocked;
}