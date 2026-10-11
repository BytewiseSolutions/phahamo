package ls.co.bytewise.api.examination;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import ls.co.bytewise.api.common.BaseEntity;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.subject.Subject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "examination_subject_class",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_exam_subject_class",
                columnNames = {"examination_id", "subject_id", "school_class_id"}
        )
)
public class ExaminationSubjectClass extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examination_id", nullable = false)
    private Examination examination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_class_id", nullable = false)
    private SchoolClass schoolClass;
}