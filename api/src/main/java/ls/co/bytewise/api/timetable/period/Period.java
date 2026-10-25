package ls.co.bytewise.api.timetable.period;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ls.co.bytewise.api.common.SchoolScopedEntity;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "period")
public class Period extends SchoolScopedEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'LESSON'")
    private PeriodType type = PeriodType.LESSON;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
}
