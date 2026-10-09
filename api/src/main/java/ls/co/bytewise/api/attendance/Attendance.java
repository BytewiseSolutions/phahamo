package ls.co.bytewise.api.attendance;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.timetable.lesson.Lesson;
import ls.co.bytewise.api.user.User;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
        name = "attendance",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_attendance_student_lesson",
                columnNames = {"student_id", "lesson_id"}
        )
)
public class Attendance extends SchoolScopedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_class_id", nullable = false)
    private SchoolClass schoolClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_by")
    private User markedBy;
}