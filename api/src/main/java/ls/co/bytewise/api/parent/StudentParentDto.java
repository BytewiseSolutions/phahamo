package ls.co.bytewise.api.parent;

import ls.co.bytewise.api.common.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class StudentParentDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private boolean loginEnabled;
    private Instant invitedAt;

    public StudentParentDto(Parent parent) {
        this.id = parent.getId();
        this.firstName = parent.getUser().getFirstName();
        this.lastName = parent.getUser().getLastName();
        this.email = parent.getUser().getEmail();
        this.phone = parent.getUser().getPhone();
        this.loginEnabled = parent.isLoginEnabled()
                && parent.getUser().getStatus() == Status.ACTIVE;
        this.invitedAt = parent.getInvitedAt();
    }
}
