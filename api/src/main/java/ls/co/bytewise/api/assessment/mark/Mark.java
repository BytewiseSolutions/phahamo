package ls.co.bytewise.api.assessment.mark;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import ls.co.bytewise.api.assessment.Assessment;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import ls.co.bytewise.api.student.Student;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "mark",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_mark_assessment_student",
                columnNames = {"assessment_id", "student_id"}
        )
)
public class Mark extends SchoolScopedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column
    private Double score;
}