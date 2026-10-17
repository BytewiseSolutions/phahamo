package ls.co.bytewise.api.studentfee;

import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StudentFeeAccountDto extends BaseDto {

    private Long studentId;
    private String studentFirstName;
    private String studentLastName;
    private String admissionNumber;

    private Long termId;
    private String termName;
    private String academicYearName;

    private Long classId;
    private String className;

    private Double totalFees;
    private Double totalAdjustments;
    private Double amountPaid;
    private Double balance;

    private List<StudentFeeLineDto> lines;
    private List<StudentFeeAdjustmentDto> adjustments;

    public StudentFeeAccountDto(StudentFeeAccount acc) {
        super(acc);
        this.studentId = acc.getStudent().getId();
        this.studentFirstName = acc.getStudent().getUser().getFirstName();
        this.studentLastName = acc.getStudent().getUser().getLastName();
        this.admissionNumber = acc.getStudent().getAdmissionNumber();

        this.termId = acc.getTerm().getId();
        this.termName = acc.getTerm().getName();
        if (acc.getTerm().getAcademicYear() != null) {
            this.academicYearName = acc.getTerm().getAcademicYear().getName();
        }

        this.classId = acc.getSchoolClass().getId();
        this.className = acc.getSchoolClass().getName();

        this.totalFees = acc.getTotalFees();
        this.totalAdjustments = acc.getTotalAdjustments();
        this.amountPaid = acc.getAmountPaid();
        this.balance = acc.getBalance();

        this.lines = acc.getLines().stream().map(StudentFeeLineDto::new).toList();
        this.adjustments = acc.getAdjustments().stream().map(StudentFeeAdjustmentDto::new).toList();
    }
}