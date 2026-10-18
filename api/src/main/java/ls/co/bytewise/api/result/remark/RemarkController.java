package ls.co.bytewise.api.result.remark;

import ls.co.bytewise.api.user.User;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/remarks")
public class RemarkController {

    private final RemarkService service;

    public RemarkController(RemarkService service) {
        this.service = service;
    }

    @GetMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<RemarkDto> list(@AuthenticationPrincipal User currentUser,
                                @RequestParam Long classId,
                                @RequestParam Long termId) {
        return service.listForClass(classId, termId, currentUser);
    }

    @PostMapping("/class-teacher/{studentId}")
    @Secured({"ROLE_TEACHER"})
    public void saveClassTeacherRemark(@AuthenticationPrincipal User currentUser,
                                       @PathVariable Long studentId,
                                       @RequestParam Long termId,
                                       @RequestBody RemarkPayloadDto payload) {
        service.saveClassTeacherRemark(currentUser, studentId, termId, payload.getRemark());
    }

    @PostMapping("/principal/{studentId}")
    @Secured({"ROLE_PRINCIPAL", "ROLE_SCHOOL_ADMIN"})
    public void savePrincipalRemark(@AuthenticationPrincipal User currentUser,
                                    @PathVariable Long studentId,
                                    @RequestParam Long termId,
                                    @RequestBody RemarkPayloadDto payload) {
        service.savePrincipalRemark(currentUser, studentId, termId, payload.getRemark());
    }
}