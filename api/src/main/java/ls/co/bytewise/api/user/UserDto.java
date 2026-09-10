package ls.co.bytewise.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import ls.co.bytewise.api.common.BaseDto;
import ls.co.bytewise.api.common.Status;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.school.SchoolDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserDto extends BaseDto {

    private String firstName;

    private String lastName;

    @Email(message = "Email must be valid")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String phone;

    private Status status;

    private Set<UserRole> roles;

    @Valid
    private SchoolDto school;

    public UserDto(User user) {
        super(user);

        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.status = user.getStatus();
        this.roles = user.getRoles();
    }
}