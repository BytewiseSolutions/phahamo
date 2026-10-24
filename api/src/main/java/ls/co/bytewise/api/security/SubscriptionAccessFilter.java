package ls.co.bytewise.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ls.co.bytewise.api.subscription.SubscriptionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SubscriptionAccessFilter extends OncePerRequestFilter {

    private static final List<String> WHITELISTED_PATHS = List.of(
            "/api/auth/",
            "/api/school/subscription-status",
            "/api/subscriptions/me",
            "/api/admin/",
            "/api/schools/register"
    );

    private final SubscriptionService subscriptionService;

    public SubscriptionAccessFilter(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (WHITELISTED_PATHS.stream().anyMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            chain.doFilter(request, response);
            return;
        }

        boolean isPrivileged = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SYSTEM_ADMIN")
                        || a.getAuthority().equals("ROLE_SCHOOL_ADMIN"));
        if (isPrivileged) {
            chain.doFilter(request, response);
            return;
        }

        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            chain.doFilter(request, response);
            return;
        }

        if (!subscriptionService.isSchoolActive(schoolId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"code\":\"SUBSCRIPTION_EXPIRED\",\"message\":\"Your school's subscription has expired or been suspended.\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}