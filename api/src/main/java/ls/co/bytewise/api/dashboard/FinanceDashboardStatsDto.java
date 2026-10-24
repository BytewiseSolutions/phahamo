package ls.co.bytewise.api.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinanceDashboardStatsDto {
    private double feesCollectedThisTerm;
    private double totalOutstanding;
    private long paymentsRecordedToday;
}
