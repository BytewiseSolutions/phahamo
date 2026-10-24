package ls.co.bytewise.api.timetable.lesson;

import jakarta.validation.constraints.NotNull;
import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class LessonDto extends BaseDto {

    @NotNull
    private Long timetableId;

    private DayOfWeek dayOfWeek;
    private String periodName;
    private LocalTime periodStartTime;
    private LocalTime periodEndTime;
    private String subjectName;
    private String schoolClassName;
    private String teacherName;

    private String substituteTeacherName;

    @NotNull
    private LocalDate date;

    private LessonStatus status;

    public LessonDto(Lesson lesson) {
        super(lesson);
        this.timetableId = lesson.getTimetable().getId();
        this.dayOfWeek = lesson.getTimetable().getDayOfWeek();
        this.periodName = lesson.getTimetable().getPeriod().getName();
        this.periodStartTime = lesson.getTimetable().getPeriod().getStartTime();
        this.periodEndTime = lesson.getTimetable().getPeriod().getEndTime();
        this.subjectName = lesson.getTimetable().getAssignment().getSubject().getName();
        this.schoolClassName = lesson.getTimetable().getAssignment().getSchoolClass().getName();

        if (lesson.getTimetable().getAssignment().getTeacher() != null) {
            this.teacherName = lesson.getTimetable().getAssignment().getTeacher().getFullName();
        }

        if (lesson.getSubstituteTeacher() != null) {
            this.substituteTeacherName = lesson.getSubstituteTeacher().getFullName();
        }

        this.date = lesson.getDate();
        this.status = lesson.getStatus();
    }
}
