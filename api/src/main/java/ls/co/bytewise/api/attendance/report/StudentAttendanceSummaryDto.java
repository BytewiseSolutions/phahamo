package ls.co.bytewise.api.attendance.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentAttendanceSummaryDto {

    private Long studentId;
    private String firstName;
    private String lastName;
    private String admissionNumber;

    private int total;
    private int present;
    private int absent;
    private int late;
    private int excused;

    private double presentPercent;
    private double absentPercent;
    private double latePercent;
    private double excusedPercent;
}