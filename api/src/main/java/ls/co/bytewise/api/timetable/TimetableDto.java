package ls.co.bytewise.api.timetable;

import jakarta.validation.constraints.NotNull;
import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class TimetableDto extends BaseDto {

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Period is required")
    private Long periodId;

    private String periodName;

    private LocalTime periodStartTime;

    private LocalTime periodEndTime;

    @NotNull(message = "Assignment is required")
    private Long assignmentId;

    private String subjectName;

    private String schoolClassName;

    private String teacherName;

    public TimetableDto(Timetable timetable) {
        super(timetable);
        this.dayOfWeek = timetable.getDayOfWeek();

        this.periodId = timetable.getPeriod().getId();
        this.periodName = timetable.getPeriod().getName();
        this.periodStartTime = timetable.getPeriod().getStartTime();
        this.periodEndTime = timetable.getPeriod().getEndTime();

        this.assignmentId = timetable.getAssignment().getId();
        this.subjectName = timetable.getAssignment().getSubject().getName();
        this.schoolClassName = timetable.getAssignment().getSchoolClass().getName();

        if (timetable.getAssignment().getTeacher() != null) {
            this.teacherName = timetable.getAssignment().getTeacher().getFullName();
        }
    }
}