package ls.co.bytewise.api.term;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ls.co.bytewise.api.academicyear.AcademicYear;
import ls.co.bytewise.api.common.SchoolScopedEntity;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "term")
public class Term extends SchoolScopedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant endDate;
}