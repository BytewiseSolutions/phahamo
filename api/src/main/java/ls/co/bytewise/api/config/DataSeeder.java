package ls.co.bytewise.api.config;

import ls.co.bytewise.api.common.Status;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@Profile("e2e")
public class DataSeeder {

    @Bean
    CommandLineRunner seedAdmin(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            String adminEmail = "monamane.lebohang45@gmail.com";
            if (users.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setFirstName("Test");
                admin.setLastName("Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(encoder.encode("password"));
                admin.setPhone("0000000000");
                admin.setStatus(Status.ACTIVE);
                admin.setRoles(Set.of(UserRole.SYSTEM_ADMIN));
                users.save(admin);
                System.out.println("[e2e] Seeded SYSTEM_ADMIN: " + adminEmail);
            }
        };
    }
}