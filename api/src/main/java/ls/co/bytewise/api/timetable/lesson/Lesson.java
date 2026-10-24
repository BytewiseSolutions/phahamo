package ls.co.bytewise.api.timetable.lesson;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.timetable.Timetable;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "lesson")
public class Lesson extends SchoolScopedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_id", nullable = false)
    private Timetable timetable;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonStatus status = LessonStatus.SCHEDULED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substitute_teacher_id")
    private User substituteTeacher;
}
