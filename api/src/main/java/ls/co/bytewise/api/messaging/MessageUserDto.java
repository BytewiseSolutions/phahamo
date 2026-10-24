package ls.co.bytewise.api.messaging;

import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.user.User;
import lombok.Getter;

import java.util.Set;

@Getter
public class MessageUserDto {
    private final Long id;
    private final String name;
    private final Set<UserRole> roles;

    public MessageUserDto(User user) {
        this.id = user.getId();
        this.name = user.getFullName();
        this.roles = user.getRoles();
    }
}
