package ls.co.bytewise.api.school.subscription;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/me")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_PRINCIPAL", "ROLE_FINANCE_OFFICER"})
    public SubscriptionDto me() {
        return subscriptionService.getForCurrentSchool();
    }

    @GetMapping
    @Secured("ROLE_SYSTEM_ADMIN")
    public List<SubscriptionDto> listAll() {
        return subscriptionService.listAll();
    }

    @GetMapping("/school/{schoolId}")
    @Secured("ROLE_SYSTEM_ADMIN")
    public SubscriptionDto getForSchool(@PathVariable Long schoolId) {
        return subscriptionService.getForSchool(schoolId);
    }

    @PostMapping("/school/{schoolId}/extend")
    @Secured("ROLE_SYSTEM_ADMIN")
    public SubscriptionDto extend(@PathVariable Long schoolId,
                                  @RequestBody SubscriptionDto dto) {
        return subscriptionService.extend(schoolId, dto.getMonths());
    }

    @PostMapping("/school/{schoolId}/change-plan")
    @Secured("ROLE_SYSTEM_ADMIN")
    public SubscriptionDto changePlan(@PathVariable Long schoolId,
                                      @RequestBody SubscriptionDto dto) {
        return subscriptionService.changePlan(schoolId, dto.getPlan(), dto.getMonths());
    }

    @PostMapping("/school/{schoolId}/cancel")
    @Secured("ROLE_SYSTEM_ADMIN")
    public SubscriptionDto cancel(@PathVariable Long schoolId) {
        return subscriptionService.cancel(schoolId);
    }
}