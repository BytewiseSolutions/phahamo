package ls.co.bytewise.api.academicyear;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class AcademicYearDto extends BaseDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Start date is required")
    private Instant startDate;

    @NotNull(message = "End date is required")
    private Instant endDate;

    private boolean active;

    public AcademicYearDto(AcademicYear academicYear) {
        super(academicYear);
        this.name = academicYear.getName();
        this.startDate = academicYear.getStartDate();
        this.endDate = academicYear.getEndDate();
        this.active = academicYear.isActive();
    }

    @AssertTrue(message = "End date must be after start date")
    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return endDate.isAfter(startDate);
    }
}