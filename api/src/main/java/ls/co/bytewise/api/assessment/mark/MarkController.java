package ls.co.bytewise.api.assessment.mark;

import ls.co.bytewise.api.user.User;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments/{assessmentId}/marks")
public class MarkController {

    private final MarkService markService;

    public MarkController(MarkService markService) {
        this.markService = markService;
    }

    @GetMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<MarkDto> list(@AuthenticationPrincipal User currentUser,
                              @PathVariable Long assessmentId) {
        return markService.listForAssessment(currentUser, assessmentId);
    }

    @PutMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<MarkDto> bulkSave(@AuthenticationPrincipal User currentUser,
                                  @PathVariable Long assessmentId,
                                  @RequestBody List<MarkDto> rows) {
        return markService.bulkSave(currentUser, assessmentId, rows);
    }
}