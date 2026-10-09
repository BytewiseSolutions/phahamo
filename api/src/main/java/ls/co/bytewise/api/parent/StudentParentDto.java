package ls.co.bytewise.api.parent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentParentDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public StudentParentDto(Parent parent) {
        this.id = parent.getId();
        this.firstName = parent.getUser().getFirstName();
        this.lastName = parent.getUser().getLastName();
        this.email = parent.getUser().getEmail();
        this.phone = parent.getUser().getPhone();
    }
}