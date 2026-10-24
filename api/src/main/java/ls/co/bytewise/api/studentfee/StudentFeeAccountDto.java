package ls.co.bytewise.api.studentfee;

import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ls.co.bytewise.api.parent.Parent;

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
    private List<StudentFeePaymentDto> payments;

    private java.time.Instant lastReminderSentAt;
    private Double lastReminderAmount;
    private boolean hasEmailParent;
    private java.util.List<ParentContactDto> parents;

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
        this.payments = acc.getPayments().stream().map(StudentFeePaymentDto::new).toList();

        this.lastReminderSentAt = acc.getLastReminderSentAt();
        this.lastReminderAmount = acc.getLastReminderAmount();

        List<ParentContactDto> parentList = new java.util.ArrayList<>();
        boolean anyEmail = false;
        if (acc.getStudent() != null && acc.getStudent().getParents() != null) {
            for (Parent p : acc.getStudent().getParents()) {
                if (p.getUser() == null) continue;
                String email = p.getUser().getEmail();
                boolean hasEmail = email != null && !email.isBlank();
                if (hasEmail) anyEmail = true;
                parentList.add(new ParentContactDto(
                        p.getUser().getFullName(),
                        email,
                        p.getUser().getPhone(),
                        hasEmail
                ));
            }
        }
        this.parents = parentList;
        this.hasEmailParent = anyEmail;
    }
}