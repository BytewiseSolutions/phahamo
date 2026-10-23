package ls.co.bytewise.api.reportcard.email;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EmailReportCardResultDto {

    private int sentCount;
    private int skippedNoEmailCount;
    private int skippedNotFinalisedCount;
    private int failedCount;

    private List<String> skippedNoEmail = new ArrayList<>();
    private List<String> skippedNotFinalised = new ArrayList<>();
    private List<String> failed = new ArrayList<>();
}