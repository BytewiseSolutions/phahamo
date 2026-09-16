package ls.co.bytewise.api.timetable;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import ls.co.bytewise.api.timetable.period.Period;
import ls.co.bytewise.api.subject.subjectclassassignment.SubjectClassAssignment;

import java.time.DayOfWeek;

@Getter
@Setter
@Entity
@Table(name = "timetable")
public class Timetable extends SchoolScopedEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    private Period period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_class_assignment_id", nullable = false)
    private SubjectClassAssignment assignment;
}