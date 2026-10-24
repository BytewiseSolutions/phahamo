package ls.co.bytewise.api.studentfee;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkFeeEmailResultDto {
    private int sentCount;
    private int skippedNoEmailCount;
    private int skippedPaidCount;
    private int failedCount;

}
