package ls.co.bytewise.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.user.User;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final String[] TENANT_EXEMPT_PREFIXES = {
            "/api/auth/",
            "/api/schools/register",
            "/api/subscriptions/me",
            "/oauth2/",
            "/login/oauth2/",
            "/error"
    };

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        for (String prefix : TENANT_EXEMPT_PREFIXES) {
            if (path.startsWith(prefix)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;

        if (principal instanceof User user) {

            if (user.hasRole(UserRole.SYSTEM_ADMIN)) {
                filterChain.doFilter(request, response);
                return;
            }

            String schoolIdHeader = request.getHeader("X-School-Id");

            if (schoolIdHeader == null || schoolIdHeader.isBlank()) {
                throw new MissingTenantException("X-School-Id header is required");
            }

            Long schoolId;
            try {
                schoolId = Long.parseLong(schoolIdHeader);
            } catch (NumberFormatException e) {
                throw new MissingTenantException("X-School-Id header must be a valid number");
            }

            boolean belongsToSchool = user.getSchools().stream()
                    .map(School::getId)
                    .anyMatch(id -> id.equals(schoolId));

            if (!belongsToSchool) {
                throw new MissingTenantException("You do not have access to this school");
            }

            TenantContext.setSchoolId(schoolId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}