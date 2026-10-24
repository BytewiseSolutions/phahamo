package ls.co.bytewise.api.subscription;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateSubscriptionRequest {
    @NotNull(message = "Plan is required")
    private SubscriptionPlan plan;

    @Min(value = 1, message = "Months must be at least 1")
    private int months;
}