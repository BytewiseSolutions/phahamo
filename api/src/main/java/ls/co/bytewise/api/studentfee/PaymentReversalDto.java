package ls.co.bytewise.api.studentfee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentReversalDto {

    @NotBlank(message = "A reversal reason is required")
    @Size(max = 500, message = "Reversal reason must be 500 characters or fewer")
    private String reason;
}
