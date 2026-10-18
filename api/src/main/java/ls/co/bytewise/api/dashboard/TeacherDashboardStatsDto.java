package ls.co.bytewise.api.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherDashboardStatsDto {
    private long totalClasses;
    private long todayPeriods;
    private long pendingAttendance;
}