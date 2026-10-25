package ls.co.bytewise.api.parent;

import lombok.Getter;

@Getter
public class ParentDashboardChildDto {
    private final ParentChildDto child;
    private final String todayAttendance;
    private final Double feeBalance;

    public ParentDashboardChildDto(ParentChildDto child, String todayAttendance, Double feeBalance) {
        this.child = child;
        this.todayAttendance = todayAttendance;
        this.feeBalance = feeBalance;
    }
}
