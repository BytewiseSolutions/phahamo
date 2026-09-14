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

    @PostMapping("/api/parents")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<ParentDto> create(@AuthenticationPrincipal User currentUser,
                                            @Valid @RequestBody ParentDto dto) {
        ParentDto created = parentService.createParent(currentUser, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/api/parents")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<ParentDto> list() {
        return parentService.listParents();
    }

    @GetMapping("/api/parents/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ParentDto get(@PathVariable Long id) {
        return parentService.getParent(id);
    }

    @PutMapping("/api/parents/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ParentDto update(@AuthenticationPrincipal User currentUser,
                            @PathVariable Long id,
                            @Valid @RequestBody ParentDto dto) {
        return parentService.updateParent(currentUser, id, dto);
    }

    @DeleteMapping("/api/parents/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        parentService.deleteParent(id);
        return ResponseEntity.noContent().build();
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

    @DeleteMapping("/api/students/{studentId}/parents/{parentId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<Void> unlink(@PathVariable Long studentId, @PathVariable Long parentId) {
        parentService.unlinkParent(studentId, parentId);
        return ResponseEntity.noContent().build();
    }
}