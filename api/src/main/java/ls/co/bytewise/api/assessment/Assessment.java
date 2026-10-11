package ls.co.bytewise.api.assessment;

import jakarta.persistence.*;
import ls.co.bytewise.api.assessment.mark.Mark;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import ls.co.bytewise.api.subject.subjectclassassignment.SubjectClassAssignment;
import ls.co.bytewise.api.term.Term;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false)
    private String name;

    @Column(name = "max_mark", nullable = false)
    private Double maxMark;

    @Column(nullable = false)
    private Integer weight;

    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mark> marks = new ArrayList<>();
}