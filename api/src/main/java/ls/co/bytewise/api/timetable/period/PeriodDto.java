package ls.co.bytewise.api.timetable.period;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class PeriodDto extends BaseDto {

    @NotBlank
    private String name;

    private PeriodType type = PeriodType.LESSON;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    public PeriodDto(Period period) {
        super(period);
        this.name = period.getName();
        this.type = period.getType() != null ? period.getType() : PeriodType.LESSON;
        this.startTime = period.getStartTime();
        this.endTime = period.getEndTime();
    }
}
