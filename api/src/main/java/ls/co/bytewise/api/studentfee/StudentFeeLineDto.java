package ls.co.bytewise.api.studentfee;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentFeeLineDto {

    private Long id;
    private String name;
    private Double amount;
    private Boolean mandatory;
    private String description;
    private Boolean fromAnnualFee;

    public StudentFeeLineDto(StudentFeeLine line) {
        this.id = line.getId();
        this.name = line.getName();
        this.amount = line.getAmount();
        this.mandatory = line.getMandatory();
        this.description = line.getDescription();
        this.fromAnnualFee = line.getFromAnnualFee();
    }
}