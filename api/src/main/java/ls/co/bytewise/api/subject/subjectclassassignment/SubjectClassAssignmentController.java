package ls.co.bytewise.api.subject.subjectclassassignment;

import jakarta.validation.Valid;
import ls.co.bytewise.api.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subject-class-assignments")
public class SubjectClassAssignmentController {

    private final SubjectClassAssignmentService assignmentService;

    public SubjectClassAssignmentController(SubjectClassAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<SubjectClassAssignmentDto> create(@AuthenticationPrincipal User currentUser,
                                                            @Valid @RequestBody SubjectClassAssignmentDto dto) {
        SubjectClassAssignmentDto created = assignmentService.createAssignment(currentUser, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<SubjectClassAssignmentDto> list() {
        return assignmentService.listAssignments();
    }

    @GetMapping("/class/{schoolClassId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<SubjectClassAssignmentDto> listByClass(@PathVariable Long schoolClassId) {
        return assignmentService.listByClass(schoolClassId);
    }

    @GetMapping("/subject/{subjectId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<SubjectClassAssignmentDto> listBySubject(@PathVariable Long subjectId) {
        return assignmentService.listBySubject(subjectId);
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public SubjectClassAssignmentDto get(@PathVariable Long id) {
        return assignmentService.getAssignment(id);
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public SubjectClassAssignmentDto update(@AuthenticationPrincipal User currentUser,
                                            @PathVariable Long id,
                                            @Valid @RequestBody SubjectClassAssignmentDto dto) {
        return assignmentService.updateAssignment(currentUser, id, dto);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }
}