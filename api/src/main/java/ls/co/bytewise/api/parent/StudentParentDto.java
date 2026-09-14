package ls.co.bytewise.api.parent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentParentDto {

    private Long parentId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public StudentParentDto(Parent parent) {
        this.parentId = parent.getId();
        this.firstName = parent.getFirstName();
        this.lastName = parent.getLastName();
        this.email = parent.getEmail();
        this.phone = parent.getPhone();
    }
}