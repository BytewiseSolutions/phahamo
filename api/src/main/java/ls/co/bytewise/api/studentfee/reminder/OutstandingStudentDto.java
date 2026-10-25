package ls.co.bytewise.api.studentfee.reminder;

import lombok.Getter;
import lombok.Setter;
import ls.co.bytewise.api.studentfee.ParentContactDto;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class OutstandingStudentDto {
    private Long accountId;
    private Long studentId;
    private String studentFullName;
    private String admissionNumber;
    private String className;
    private Double balance;
    private Double previousBalance;
    private Instant lastReminderSentAt;
    private Double lastReminderAmount;
    private List<ParentContactDto> parents;
    private boolean hasEmailParent;
}
