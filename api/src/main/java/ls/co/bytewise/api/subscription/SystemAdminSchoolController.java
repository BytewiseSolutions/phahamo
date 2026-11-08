package ls.co.bytewise.api.subscription;

import jakarta.validation.Valid;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/admin/schools")
@Secured("ROLE_SYSTEM_ADMIN")
public class SystemAdminSchoolController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;
    private final SchoolRepository schoolRepository;

    public SystemAdminSchoolController(SubscriptionService subscriptionService,
                                       SubscriptionRepository subscriptionRepository,
                                       SchoolRepository schoolRepository) {
        this.subscriptionService = subscriptionService;
        this.subscriptionRepository = subscriptionRepository;
        this.schoolRepository = schoolRepository;
    }

    @GetMapping
    public List<SystemAdminSchoolDto> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) SubscriptionStatus status) {
        return subscriptionRepository.search(search, status).stream()
                .map(this::toDto)
                .toList();
    }

    @PostMapping("/{schoolId}/extend-trial")
    public SubscriptionDto extendTrial(@PathVariable Long schoolId,
                                       @Valid @RequestBody ExtendTrialRequest request) {
        return subscriptionService.extendTrial(schoolId, request.getDays());
    }

    @PostMapping("/{schoolId}/activate")
    public SubscriptionDto activate(@PathVariable Long schoolId,
                                    @Valid @RequestBody ActivateSubscriptionRequest request) {
        return subscriptionService.activate(
                schoolId, request.getPlan(), request.getMonths());
    }

    @PostMapping("/{schoolId}/suspend")
    public SubscriptionDto suspend(@PathVariable Long schoolId) {
        return subscriptionService.suspend(schoolId);
    }

    private SystemAdminSchoolDto toDto(Subscription sub) {
        SystemAdminSchoolDto dto = new SystemAdminSchoolDto();
        School school = sub.getSchool();
        dto.setSchoolId(school.getId());
        dto.setSchoolName(school.getName());
        dto.setContactEmail(school.getContactEmail());
        dto.setSubscriptionId(sub.getId());
        dto.setPlan(sub.getPlan());
        dto.setStatus(subscriptionService.computeEffectiveStatus(sub));
        dto.setStartedAt(sub.getStartedAt());
        dto.setEndsAt(sub.getEndsAt());
        dto.setDaysRemaining(computeDaysRemaining(sub.getEndsAt()));
        return dto;
    }

    private Long computeDaysRemaining(Instant endsAt) {
        if (endsAt == null) return null;
        long days = ChronoUnit.DAYS.between(Instant.now(), endsAt);
        return Math.max(days, 0);
    }
}
