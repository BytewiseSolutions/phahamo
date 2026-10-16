package ls.co.bytewise.api.attendance.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class LessonAttendanceSummaryDto {

    private Long lessonId;
    private LocalDate date;
    private String periodName;
    private String subjectName;
    private String className;

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