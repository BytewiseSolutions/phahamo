package ls.co.bytewise.api.user.auth;

import ls.co.bytewise.api.common.Status;
import ls.co.bytewise.api.security.JwtService;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.user.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (user.getStatus() != Status.ACTIVE) {
            throw new BadCredentialsException("Account is " + user.getStatus().name().toLowerCase());
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getFullName(),
                user.getRoles(),
                user.getSchools().stream().map(school -> school.getId()).collect(Collectors.toSet())
        );
    }
}