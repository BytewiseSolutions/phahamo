package ls.co.bytewise.api.term;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ls.co.bytewise.api.common.BaseDto;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class TermDto extends BaseDto {

    @NotNull(message = "Academic year is required")
    private Long academicYearId;

    private String academicYearName;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Start date is required")
    private Instant startDate;

    @NotNull(message = "End date is required")
    private Instant endDate;

    public TermDto(Term term) {
        super(term);
        this.academicYearId = term.getAcademicYear().getId();
        this.academicYearName = term.getAcademicYear().getName();
        this.name = term.getName();
        this.startDate = term.getStartDate();
        this.endDate = term.getEndDate();
    }

    @AssertTrue(message = "End date must be after start date")
    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return endDate.isAfter(startDate);
    }
}