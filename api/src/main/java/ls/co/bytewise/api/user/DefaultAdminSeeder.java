package ls.co.bytewise.api.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DefaultAdminSeeder implements ApplicationRunner {
    private final UserService userService;
    private final String email;
    private final String password;

    public DefaultAdminSeeder(UserService userService,
                              @Value("${app.bootstrap-admin.email:}") String email,
                              @Value("${app.bootstrap-admin.password:}") String password) {
        this.userService = userService;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        userService.seedDefaultAdmin(email, password);
    }
}
