package ls.co.bytewise.api.schoolclass;

import jakarta.validation.Valid;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class SchoolClassController {

    private final SchoolClassService schoolClassService;

    public SchoolClassController(SchoolClassService schoolClassService) {
        this.schoolClassService = schoolClassService;
    }

    @PostMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<SchoolClassDto> create(@AuthenticationPrincipal User currentUser,
                                                 @Valid @RequestBody SchoolClassDto dto) {
        SchoolClassDto created = schoolClassService.createClass(currentUser, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<SchoolClassDto> list() {
        return schoolClassService.listClasses();
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public SchoolClassDto get(@PathVariable Long id) {
        return schoolClassService.getClass(id);
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public SchoolClassDto update(@AuthenticationPrincipal User currentUser,
                                 @PathVariable Long id,
                                 @Valid @RequestBody SchoolClassDto dto) {
        return schoolClassService.updateClass(currentUser, id, dto);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        schoolClassService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-class")
    @Secured({"ROLE_TEACHER"})
    public SchoolClassDto getMyClass(@AuthenticationPrincipal User currentUser) {
        return schoolClassService.getMyClass(currentUser);
    }

    @GetMapping("/my-classes")
    @Secured({"ROLE_TEACHER"})
    public List<SchoolClassDto> myClasses(@AuthenticationPrincipal User currentUser) {
        Long schoolId = TenantContext.getSchoolId();
        return schoolClassService.listByClassTeacher(currentUser, schoolId);
    }
}