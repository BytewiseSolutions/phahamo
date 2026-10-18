package ls.co.bytewise.api.dashboard;

import lombok.Getter;
import lombok.Setter;
import ls.co.bytewise.api.timetable.TimetableDto;
import java.util.List;

@Getter
@Setter
public class StudentDashboardStatsDto {
    private List<TimetableDto> todayTimetable;
    private Double attendanceRateTerm;
    private String latestResultSubject;
    private String latestResultGrade;
    private Double latestResultMark;
    private Double feeBalance;
}