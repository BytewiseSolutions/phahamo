package ls.co.bytewise.api.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemDashboardStats {
    private long totalSchools;
    private long trialSchools;
    private long activeSubscriptionSchools;
    private long trialsEndingNext14Days;
    private long newSchoolsThisMonth;
}
