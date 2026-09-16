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

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    private Integer orderIndex;

    public PeriodDto(Period period) {
        super(period);
        this.name = period.getName();
        this.startTime = period.getStartTime();
        this.endTime = period.getEndTime();
        this.orderIndex = period.getOrderIndex();
    }
}