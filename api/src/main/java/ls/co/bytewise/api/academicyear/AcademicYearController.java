package ls.co.bytewise.api.academicyear;

import jakarta.validation.Valid;
import ls.co.bytewise.api.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academic-years")
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    public AcademicYearController(AcademicYearService academicYearService) {
        this.academicYearService = academicYearService;
    }

    @PostMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<AcademicYearDto> create(@AuthenticationPrincipal User currentUser,
                                                  @Valid @RequestBody AcademicYearDto dto) {
        AcademicYearDto created = academicYearService.createAcademicYear(currentUser, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<AcademicYearDto> list() {
        return academicYearService.listAcademicYears();
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public AcademicYearDto get(@PathVariable Long id) {
        return academicYearService.getAcademicYear(id);
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public AcademicYearDto update(@AuthenticationPrincipal User currentUser,
                                  @PathVariable Long id,
                                  @Valid @RequestBody AcademicYearDto dto) {
        return academicYearService.updateAcademicYear(currentUser, id, dto);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        academicYearService.deleteAcademicYear(id);
        return ResponseEntity.noContent().build();
    }
}