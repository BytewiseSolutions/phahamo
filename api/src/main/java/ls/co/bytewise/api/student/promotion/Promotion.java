package ls.co.bytewise.api.student.promotion;

import jakarta.persistence.*;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentStatus;
import ls.co.bytewise.api.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "promotion")
public class Promotion extends SchoolScopedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_class_id")
    private SchoolClass fromClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_class_id")
    private SchoolClass toClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false)
    private StudentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private StudentStatus newStatus;

    @Column(length = 1000, nullable = false)
    private String reason;

    @Column(name = "promoted_at", nullable = false)
    private Instant promotedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promoted_by")
    private User promotedBy;

    @Column(name = "batch_id", length = 36, nullable = false)
    private String batchId;
}