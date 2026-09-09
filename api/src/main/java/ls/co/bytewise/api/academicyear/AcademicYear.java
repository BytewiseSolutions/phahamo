package ls.co.bytewise.api.academicyear;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ls.co.bytewise.api.common.SchoolScopedEntity;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "academic_year")
public class AcademicYear extends SchoolScopedEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant endDate;

    @Column(nullable = false)
    private boolean active = false;
}