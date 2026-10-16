package ls.co.bytewise.api.result;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.subject.Subject;
import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
        name = "term_result",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_term_result",
                columnNames = {"student_id", "subject_id", "term_id"}
        )
)
public class TermResult extends SchoolScopedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_class_id", nullable = false)
    private SchoolClass schoolClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @Column(name = "final_mark", nullable = false)
    private Double finalMark;

    private String grade;

    private String remark;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TermResultStatus status = TermResultStatus.CALCULATED;

    @Column(name = "calculated_at", nullable = false)
    private Instant calculatedAt;

    @Column(name = "finalised_at")
    private Instant finalisedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finalised_by")
    private User finalisedBy;
}