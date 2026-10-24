package ls.co.bytewise.api.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemDashboardStats {
    private long totalSchools;
    private long activeSchools;
    private long trialSchools;
    private long suspendedSchools;
    private long expiringSoonSchools;
    private long totalUsers;
}