package ls.co.bytewise.api.attendance;

import jakarta.persistence.*;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import ls.co.bytewise.api.student.Student;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
        name = "absence_alert_log",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_absence_alert_student_date",
                columnNames = {"student_id", "absence_date"}
        )
)
public class AbsenceAlertLog extends SchoolScopedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "absence_date", nullable = false)
    private LocalDate absenceDate;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;
}