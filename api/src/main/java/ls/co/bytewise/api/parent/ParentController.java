package ls.co.bytewise.api.parent;

import jakarta.validation.Valid;
import ls.co.bytewise.api.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ParentController {

    private final ParentService parentService;

    public ParentController(ParentService parentService) {
        this.parentService = parentService;
    }

    @GetMapping("/api/students/{studentId}/parents")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<StudentParentDto> listForStudent(@PathVariable Long studentId) {
        return parentService.listParentsForStudent(studentId);
    }

    @PostMapping("/api/students/{studentId}/parents")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<StudentParentDto> createAndLink(@AuthenticationPrincipal User currentUser,
                                                          @PathVariable Long studentId,
                                                          @Valid @RequestBody ParentDto dto) {
        StudentParentDto created = parentService.createAndLinkParent(currentUser, studentId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/api/students/{studentId}/parents/{parentId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<StudentParentDto> linkExisting(@AuthenticationPrincipal User currentUser,
                                                         @PathVariable Long studentId,
                                                         @PathVariable Long parentId) {
        StudentParentDto linked = parentService.linkExistingParent(currentUser, studentId, parentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(linked);
    }

    @PutMapping("/api/students/{studentId}/parents/{parentId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public StudentParentDto update(@AuthenticationPrincipal User currentUser,
                                   @PathVariable Long studentId,
                                   @PathVariable Long parentId,
                                   @Valid @RequestBody ParentDto dto) {
        return parentService.updateParent(currentUser, studentId, parentId, dto);
    }

    @DeleteMapping("/api/students/{studentId}/parents/{parentId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<Void> unlink(@PathVariable Long studentId, @PathVariable Long parentId) {
        parentService.unlinkParent(studentId, parentId);
        return ResponseEntity.noContent().build();
    }
}