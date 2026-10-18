package ls.co.bytewise.api.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceReportRowDto {
    private String className;
    private Long totalRecords;
    private Double presentPercent;
    private Double absentPercent;
    private Double latePercent;
}