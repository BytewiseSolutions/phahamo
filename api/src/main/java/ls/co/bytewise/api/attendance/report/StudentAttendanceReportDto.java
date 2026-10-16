package ls.co.bytewise.api.attendance.report;

import ls.co.bytewise.api.attendance.AttendanceDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StudentAttendanceReportDto {

    private StudentAttendanceSummaryDto summary;
    private List<AttendanceDto> records = new ArrayList<>();
}