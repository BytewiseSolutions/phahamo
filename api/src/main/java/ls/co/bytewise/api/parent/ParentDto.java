package ls.co.bytewise.api.parent;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParentDto extends BaseDto {

    private Long userId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private ParentRelationship relationship;

    public ParentDto(Parent parent) {
        super(parent);

        if (parent.getUser() != null) {
            this.userId = parent.getUser().getId();
            this.firstName = parent.getUser().getFirstName();
            this.lastName = parent.getUser().getLastName();
            this.email = parent.getUser().getEmail();
            this.phone = parent.getUser().getPhone();
        }

        this.relationship = parent.getRelationship();
    }
}