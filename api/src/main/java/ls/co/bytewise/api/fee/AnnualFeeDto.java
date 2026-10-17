package ls.co.bytewise.api.fee;

import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AnnualFeeDto extends BaseDto {

    private Long classId;
    private String className;
    private Long academicYearId;
    private String academicYearName;
    private String name;
    private Double amount;
    private String description;

    public AnnualFeeDto(AnnualFee fee) {
        super(fee);
        this.classId = fee.getSchoolClass().getId();
        this.className = fee.getSchoolClass().getName();
        this.academicYearId = fee.getAcademicYear().getId();
        this.academicYearName = fee.getAcademicYear().getName();
        this.name = fee.getName();
        this.amount = fee.getAmount();
        this.description = fee.getDescription();
    }
}