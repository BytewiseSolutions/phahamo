package ls.co.bytewise.api.studentfee;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreviousBalanceDto {
    private Long accountId;
    private Long termId;
    private String termName;
    private String academicYearName;
    private Double amount;

    public PreviousBalanceDto(StudentFeeAccount account, Double amount) {
        this.accountId = account.getId();
        this.termId = account.getTerm().getId();
        this.termName = account.getTerm().getName();
        this.academicYearName = account.getTerm().getAcademicYear() != null
                ? account.getTerm().getAcademicYear().getName() : null;
        this.amount = amount;
    }
}
