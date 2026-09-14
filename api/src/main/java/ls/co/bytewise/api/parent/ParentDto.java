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

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    public ParentDto(Parent parent) {
        super(parent);
        this.firstName = parent.getFirstName();
        this.lastName = parent.getLastName();
        this.email = parent.getEmail();
        this.phone = parent.getPhone();
    }
}