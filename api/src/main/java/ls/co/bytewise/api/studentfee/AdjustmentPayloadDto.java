package ls.co.bytewise.api.studentfee;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdjustmentPayloadDto {

    private AdjustmentType type;
    private Double amount;
    private String reason;
}