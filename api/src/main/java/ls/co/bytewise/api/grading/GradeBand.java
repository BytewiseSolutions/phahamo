package ls.co.bytewise.api.grading;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "grade_band",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_grade_band_school_grade",
                columnNames = {"school_id", "grade"}
        )
)
public class GradeBand extends SchoolScopedEntity {

    @Column(name = "min_score", nullable = false)
    private Double minScore;

    @Column(name = "max_score", nullable = false)
    private Double maxScore;

    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private String remark;
}