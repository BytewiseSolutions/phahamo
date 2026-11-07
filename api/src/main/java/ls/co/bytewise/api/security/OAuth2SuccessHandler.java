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
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final String frontendUrl;

    public OAuth2SuccessHandler(UserRepository userRepository,
                                AuthenticationService authenticationService,
                                @Value("${app.frontend-url:http://localhost:4200}") String frontendUrl) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.frontendUrl = frontendUrl.replaceAll("/$", "");
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
                    .fromUriString(frontendUrl + "/login")
                    .queryParam("error", "no_account")
                    .build().toUriString());
            return;
        }

        if (user.getStatus() != Status.ACTIVE) {
            response.sendRedirect(UriComponentsBuilder
                    .fromUriString(frontendUrl + "/login")
                    .queryParam("error", "inactive")
                    .build().toUriString());
            return;
        }

        LoginResponse loginResponse = authenticationService.issueLoginResponse(user);

        response.sendRedirect(UriComponentsBuilder
                .fromUriString(frontendUrl + "/auth/callback")
                .queryParam("token", loginResponse.getToken())
                .build().toUriString());
    }
}
