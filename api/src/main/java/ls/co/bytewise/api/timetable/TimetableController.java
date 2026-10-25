package ls.co.bytewise.api.timetable;

import jakarta.validation.Valid;
import ls.co.bytewise.api.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

    private final TimetableService timetableService;

    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @GetMapping("/publication")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public TimetablePublicationDto publication() {
        return timetableService.publication();
    }

    @PostMapping("/publish")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public TimetablePublicationDto publish(
            @AuthenticationPrincipal User currentUser,
            @RequestBody(required = false) TimetablePublishRequest request) {
        return timetableService.publish(currentUser, request != null && request.isConfirmProblems());
    }

    @PostMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public ResponseEntity<TimetableDto> createTimetable(@AuthenticationPrincipal User currentUser,
                                                        @Valid @RequestBody TimetableDto dto) {
        TimetableDto created = timetableService.createTimetable(currentUser, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public TimetableDto updateTimetable(@AuthenticationPrincipal User currentUser,
                                        @PathVariable Long id,
                                        @Valid @RequestBody TimetableDto dto) {
        return timetableService.updateTimetable(currentUser, id, dto);
    }

    @GetMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<TimetableDto> listAll(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return timetableService.listAll(date);
    }

    @GetMapping("/teacher/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<TimetableDto> listByTeacher(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return timetableService.listByTeacher(currentUser, id, date);
    }

    @GetMapping("/class/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<TimetableDto> listByClass(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        return timetableService.listByClass(currentUser, id);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public ResponseEntity<Void> deleteTimetable(@AuthenticationPrincipal User currentUser,
                                                @PathVariable Long id) {
        timetableService.deleteTimetable(currentUser, id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/my-class")
    @Secured({"ROLE_STUDENT"})
    public List<TimetableDto> myClassTimetable(@AuthenticationPrincipal User currentUser) {
        return timetableService.listForStudent(currentUser);
    }
}
