package ls.co.bytewise.api.school.subscription.request;

import ls.co.bytewise.api.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subscription-requests")
public class SubscriptionRequestController {

    private final SubscriptionRequestService requestService;

    public SubscriptionRequestController(SubscriptionRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    @Secured({
            "ROLE_SCHOOL_ADMIN",
            "ROLE_PRINCIPAL",
            "ROLE_FINANCE_OFFICER"
    })
    public ResponseEntity<SubscriptionRequestDto> create(
            @AuthenticationPrincipal User currentUser,
            @RequestBody SubscriptionRequestDto dto) {
        SubscriptionRequestDto created = requestService.create(currentUser, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/my-school")
    @Secured({
            "ROLE_SCHOOL_ADMIN",
            "ROLE_PRINCIPAL",
            "ROLE_FINANCE_OFFICER"
    })
    public List<SubscriptionRequestDto> listForCurrentSchool() {
        return requestService.listForCurrentSchool();
    }

    @GetMapping
    @Secured("ROLE_SYSTEM_ADMIN")
    public List<SubscriptionRequestDto> listAll() {
        return requestService.listAll();
    }

    @GetMapping("/pending")
    @Secured("ROLE_SYSTEM_ADMIN")
    public List<SubscriptionRequestDto> listPending() {
        return requestService.listPending();
    }

    @PostMapping("/{id}/approve")
    @Secured("ROLE_SYSTEM_ADMIN")
    public SubscriptionRequestDto approve(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @RequestBody SubscriptionRequestDto dto) {
        return requestService.approve(currentUser, id, dto.getReviewNote());
    }

    @PostMapping("/{id}/reject")
    @Secured("ROLE_SYSTEM_ADMIN")
    public SubscriptionRequestDto reject(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @RequestBody SubscriptionRequestDto dto) {
        return requestService.reject(currentUser, id, dto.getReviewNote());
    }

    @DeleteMapping("/{id}")
    @Secured({
            "ROLE_SCHOOL_ADMIN",
            "ROLE_PRINCIPAL",
            "ROLE_FINANCE_OFFICER"
    })
    public SubscriptionRequestDto cancel(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        return requestService.cancel(currentUser, id);
    }

    @GetMapping("/school/{schoolId}")
    @Secured("ROLE_SYSTEM_ADMIN")
    public List<SubscriptionRequestDto> listForSchool(@PathVariable Long schoolId) {
        return requestService.listForSchool(schoolId);
    }
}