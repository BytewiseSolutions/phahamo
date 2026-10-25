package ls.co.bytewise.api.holiday;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ls.co.bytewise.api.common.BaseDto;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class HolidayDto extends BaseDto {

    @NotBlank(message = "Holiday name is required")
    private String name;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private boolean recurring;

    public HolidayDto(Holiday holiday) {
        super(holiday);
        this.name = holiday.getName();
        this.startDate = holiday.getStartDate();
        this.endDate = holiday.getEndDate();
        this.recurring = holiday.isRecurring();
    }

    @AssertTrue(message = "End date must be on or after start date")
    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) return true;
        return !endDate.isBefore(startDate);
    }
}
