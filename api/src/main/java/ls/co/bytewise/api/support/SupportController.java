package ls.co.bytewise.api.support;

import jakarta.validation.Valid;
import ls.co.bytewise.api.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    @PostMapping
    public ResponseEntity<Void> contactSupport(@AuthenticationPrincipal User user,
                                               @Valid @RequestBody SupportRequest request) {
        supportService.contactSupport(user, request);
        return ResponseEntity.accepted().build();
    }
}
