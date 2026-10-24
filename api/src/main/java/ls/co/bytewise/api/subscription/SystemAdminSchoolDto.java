package ls.co.bytewise.api.subscription;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class SystemAdminSchoolDto {
    private Long schoolId;
    private String schoolName;
    private String contactEmail;
    private Long subscriptionId;
    private SubscriptionPlan plan;
    private SubscriptionStatus status;
    private Instant startedAt;
    private Instant endsAt;
    private Long daysRemaining;
}