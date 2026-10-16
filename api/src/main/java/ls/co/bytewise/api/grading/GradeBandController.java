package ls.co.bytewise.api.grading;

import ls.co.bytewise.api.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grade-bands")
public class GradeBandController {

    private final GradeBandService gradeBandService;

    public GradeBandController(GradeBandService gradeBandService) {
        this.gradeBandService = gradeBandService;
    }

    @GetMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<GradeBandDto> list() {
        return gradeBandService.listAll();
    }

    @PutMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<GradeBandDto> replaceAll(@AuthenticationPrincipal User currentUser,
                                         @RequestBody List<GradeBandDto> bands) {
        return gradeBandService.replaceAll(currentUser, bands);
    }

    @GetMapping("/resolve")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public GradeResolveDto resolve(@RequestParam Double score) {
        return gradeBandService.resolve(score);
    }
}