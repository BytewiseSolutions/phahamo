package ls.co.bytewise.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import ls.co.bytewise.api.common.BaseDto;
import ls.co.bytewise.api.common.Status;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.parent.ParentDto;
import ls.co.bytewise.api.school.SchoolDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ls.co.bytewise.api.student.StudentDto;
import ls.co.bytewise.api.teacher.TeacherDto;

import java.util.Set;
import java.time.Instant;

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
    private Instant dateOfBirth;
    private Status status;
    private Instant lockedUntil;
    private Set<UserRole> roles;

    @Valid
    private SchoolDto school;

    @Valid
    private StudentDto student;

    @Valid
    private ParentDto parent;

    @Valid
    private TeacherDto teacher;

    public UserDto(User user) {
        super(user);
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.dateOfBirth = user.getDateOfBirth();
        this.status = user.getStatus();
        this.lockedUntil = user.getLockedUntil();
        this.roles = user.getRoles();
    }
}
