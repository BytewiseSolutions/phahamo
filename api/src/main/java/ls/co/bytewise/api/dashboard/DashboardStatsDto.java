package ls.co.bytewise.api.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardStatsDto {
    private long totalStudents;
    private long totalTeachers;
    private double attendanceRateTerm;
    private double feeCollectionRateTerm;
}