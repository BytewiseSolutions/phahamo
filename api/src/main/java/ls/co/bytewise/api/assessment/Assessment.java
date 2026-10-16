package ls.co.bytewise.api.assessment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import ls.co.bytewise.api.examination.Examination;
import ls.co.bytewise.api.subject.subjectclassassignment.SubjectClassAssignment;
import ls.co.bytewise.api.term.Term;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "assessment")
public class Assessment extends SchoolScopedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private SubjectClassAssignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssessmentType type = AssessmentType.CONTINUOUS;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examination_id")
    private Examination examination;

    @Column(nullable = false)
    private String name;

    @Column(name = "max_mark", nullable = false)
    private Double maxMark;

    @Column(nullable = false)
    private Integer weight;

    @Column(nullable = false)
    private LocalDate date;
}