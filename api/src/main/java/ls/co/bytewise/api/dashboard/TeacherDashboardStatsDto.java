package ls.co.bytewise.api.dashboard;

import ls.co.bytewise.api.timetable.lesson.LessonDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeacherDashboardStatsDto {
    private long totalClasses;
    private long todayPeriods;
    private long pendingAttendance;
    private long missingMarks;
    private List<LessonDto> todayLessons = List.of();
    private List<TeacherAssessmentTaskDto> assessmentsMissingMarks = List.of();
}
