package ls.co.bytewise.api.studentfee;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FeeReminderResultDto {
    private int sentCount;
    private int skippedNoEmailCount;
    private int skippedPaidCount;
    private int failedCount;

    private List<String> skippedNoEmail = new ArrayList<>();
    private List<String> skippedPaid = new ArrayList<>();
    private List<String> failed = new ArrayList<>();
}