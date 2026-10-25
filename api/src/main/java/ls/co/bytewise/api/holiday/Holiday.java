package ls.co.bytewise.api.holiday;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ls.co.bytewise.api.common.SchoolScopedEntity;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "school_holiday")
public class Holiday extends SchoolScopedEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
}
