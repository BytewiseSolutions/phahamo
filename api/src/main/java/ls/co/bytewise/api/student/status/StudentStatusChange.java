package ls.co.bytewise.api.student.status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import lombok.Getter;
import lombok.Setter;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentStatus;

@Getter
@Setter
@Entity
@Table(name = "student_status_change")
public class StudentStatusChange extends SchoolScopedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentStatus newStatus;

    @Column(nullable = false)
    private String reason;
}
