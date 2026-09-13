package ls.co.bytewise.api.student.status;

import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ls.co.bytewise.api.student.StudentStatus;

@Getter
@Setter
@NoArgsConstructor
public class StudentStatusChangeDto extends BaseDto {

    private StudentStatus previousStatus;
    private StudentStatus newStatus;
    private String reason;

    public StudentStatusChangeDto(StudentStatusChange change) {
        super(change);
        this.previousStatus = change.getPreviousStatus();
        this.newStatus = change.getNewStatus();
        this.reason = change.getReason();
    }
}