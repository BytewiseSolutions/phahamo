package ls.co.bytewise.api.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ls.co.bytewise.api.common.Status;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.user.UserRepository;
import ls.co.bytewise.api.user.auth.AuthenticationService;
import ls.co.bytewise.api.user.auth.LoginResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final String FRONTEND_CALLBACK = "http://localhost:4200/auth/callback";
    private static final String FRONTEND_LOGIN    = "http://localhost:4200/login";

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    public OAuth2SuccessHandler(UserRepository userRepository,
                                AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OidcUser oidc = (OidcUser) authentication.getPrincipal();
        String email = oidc.getEmail();

        // LOGIN ONLY — never create a user here.
        User user = (email == null) ? null : userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            response.sendRedirect(UriComponentsBuilder
                    .fromUriString(FRONTEND_LOGIN)
                    .queryParam("error", "no_account")
                    .build().toUriString());
            return;
        }

        if (user.getStatus() != Status.ACTIVE) {
            response.sendRedirect(UriComponentsBuilder
                    .fromUriString(FRONTEND_LOGIN)
                    .queryParam("error", "inactive")
                    .build().toUriString());
            return;
        }

        LoginResponse loginResponse = authenticationService.issueLoginResponse(user);

        response.sendRedirect(UriComponentsBuilder
                .fromUriString(FRONTEND_CALLBACK)
                .queryParam("token", loginResponse.getToken())
                .build().toUriString());
    }
}