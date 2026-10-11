package ls.co.bytewise.api.assessment;

import ls.co.bytewise.api.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping
    @Secured("ROLE_TEACHER")
    public ResponseEntity<AssessmentDto> create(@AuthenticationPrincipal User currentUser,
                                                @RequestBody AssessmentDto dto) {
        AssessmentDto created = assessmentService.create(currentUser, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<AssessmentDto> list(@AuthenticationPrincipal User currentUser,
                                    @RequestParam(required = false) Long termId,
                                    @RequestParam(required = false) Long assignmentId) {

        if (assignmentId != null) {
            return assessmentService.listByAssignment(assignmentId);
        }
        if (termId != null) {
            return assessmentService.listByTerm(termId);
        }

        if (currentUser.hasRole(ls.co.bytewise.api.common.UserRole.TEACHER)
                && !currentUser.hasRole(ls.co.bytewise.api.common.UserRole.SCHOOL_ADMIN)
                && !currentUser.hasRole(ls.co.bytewise.api.common.UserRole.PRINCIPAL)
                && !currentUser.hasRole(ls.co.bytewise.api.common.UserRole.SYSTEM_ADMIN)) {
            return assessmentService.listMine(currentUser);
        }

        return assessmentService.listAll();
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public AssessmentDto get(@PathVariable Long id) {
        return assessmentService.get(id);
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public AssessmentDto update(@AuthenticationPrincipal User currentUser,
                                @PathVariable Long id,
                                @RequestBody AssessmentDto dto) {
        return assessmentService.update(currentUser, id, dto);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User currentUser,
                                       @PathVariable Long id) {
        assessmentService.delete(currentUser, id);
        return ResponseEntity.noContent().build();
    }
}