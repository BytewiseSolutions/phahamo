package ls.co.bytewise.api.attendance;

import jakarta.validation.constraints.NotNull;
import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceDto extends BaseDto {

    @NotNull
    private Long studentId;
    private String studentFirstName;
    private String studentLastName;

    private Long schoolClassId;
    private String schoolClassName;

    private Long lessonId;

    private LocalDate date;

    @NotNull
    private AttendanceStatus status;

    public AttendanceDto(Attendance attendance) {
        super(attendance);
        this.studentId = attendance.getStudent().getId();
        this.studentFirstName = attendance.getStudent().getFirstName();
        this.studentLastName = attendance.getStudent().getLastName();
        this.schoolClassId = attendance.getSchoolClass().getId();
        this.schoolClassName = attendance.getSchoolClass().getName();
        this.lessonId = attendance.getLesson().getId();
        this.date = attendance.getDate();
        this.status = attendance.getStatus();
    }
}