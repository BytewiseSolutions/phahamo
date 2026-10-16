package ls.co.bytewise.api.examination;

import ls.co.bytewise.api.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/examinations")
public class ExaminationController {

    private final ExaminationService examinationService;

    public ExaminationController(ExaminationService examinationService) {
        this.examinationService = examinationService;
    }

    @PostMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<ExaminationDto> create(@AuthenticationPrincipal User currentUser,
                                                 @RequestBody ExaminationDto dto) {
        ExaminationDto created = examinationService.create(currentUser, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<ExaminationDto> list(@RequestParam(required = false) Long termId) {
        if (termId != null) {
            return examinationService.listByTerm(termId);
        }
        return examinationService.listAll();
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public ExaminationDto get(@PathVariable Long id) {
        return examinationService.get(id);
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ExaminationDto update(@AuthenticationPrincipal User currentUser,
                                 @PathVariable Long id,
                                 @RequestBody ExaminationDto dto) {
        return examinationService.update(currentUser, id, dto);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        examinationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}