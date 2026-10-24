package ls.co.bytewise.api.subscription;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtendTrialRequest {
    @Min(value = 1, message = "Days must be at least 1")
    private int days;
}