package ls.co.bytewise.api.school.subscription;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/me")
    @Secured({
            "ROLE_SCHOOL_ADMIN",
            "ROLE_PRINCIPAL",
            "ROLE_FINANCE_OFFICER"
    })
    public SubscriptionDto me() {
        return subscriptionService.getForCurrentSchool();
    }
}