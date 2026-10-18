package ls.co.bytewise.api.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinancialReportRowDto {
    private String className;
    private Double expectedFees;
    private Double collectedFees;
    private Double outstandingFees;
}