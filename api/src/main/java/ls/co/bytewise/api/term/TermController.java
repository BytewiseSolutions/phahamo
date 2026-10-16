package ls.co.bytewise.api.term;

import jakarta.validation.Valid;
import ls.co.bytewise.api.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
public class TermController {

    private final TermService termService;

    public TermController(TermService termService) {
        this.termService = termService;
    }

    @PostMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<TermDto> create(@AuthenticationPrincipal User currentUser,
                                          @Valid @RequestBody TermDto dto) {
        TermDto created = termService.createTerm(currentUser, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER", "ROLE_STUDENT"})
    public List<TermDto> list() {
        return termService.listForCurrentSchool();
    }

    @GetMapping("/academic-year/{academicYearId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<TermDto> listByAcademicYear(@PathVariable Long academicYearId) {
        return termService.getTermsByAcademicYear(academicYearId);
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public TermDto get(@PathVariable Long id) {
        return termService.getTerm(id);
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public TermDto update(@AuthenticationPrincipal User currentUser,
                          @PathVariable Long id,
                          @Valid @RequestBody TermDto dto) {
        return termService.updateTerm(currentUser, id, dto);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        termService.deleteTerm(id);
        return ResponseEntity.noContent().build();
    }
}