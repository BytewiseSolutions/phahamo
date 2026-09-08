package ls.co.bytewise.api.user.auth;

import ls.co.bytewise.api.common.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private Long userId;
    private String fullName;
    private Set<UserRole> roles;
    private Set<Long> schoolIds;
    private String schoolName;
    private boolean mustChangePassword;
}