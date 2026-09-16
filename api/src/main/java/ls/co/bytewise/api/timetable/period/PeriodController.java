package ls.co.bytewise.api.timetable.period;

import jakarta.validation.Valid;
import ls.co.bytewise.api.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/periods")
public class PeriodController {

    private final PeriodService periodService;

    public PeriodController(PeriodService periodService) {
        this.periodService = periodService;
    }

    @PostMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public ResponseEntity<PeriodDto> createPeriod(@AuthenticationPrincipal User currentUser,
                                                  @Valid @RequestBody PeriodDto dto) {
        PeriodDto created = periodService.createPeriod(currentUser, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<PeriodDto> listPeriods() {
        return periodService.listPeriods();
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public PeriodDto getPeriod(@PathVariable Long id) {
        return periodService.getPeriod(id);
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public PeriodDto updatePeriod(@AuthenticationPrincipal User currentUser,
                                  @PathVariable Long id,
                                  @Valid @RequestBody PeriodDto dto) {
        return periodService.updatePeriod(currentUser, id, dto);
    }
}