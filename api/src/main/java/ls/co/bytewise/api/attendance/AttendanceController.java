package ls.co.bytewise.api.attendance;

import jakarta.validation.Valid;
import ls.co.bytewise.api.user.User;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/class/{classId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<AttendanceDto> getAttendance(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return attendanceService.getAttendanceForClassAndDate(currentUser, classId, date);
    }

    @PostMapping("/class/{classId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<AttendanceDto> markBulk(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody List<AttendanceDto> records
    ) {
        return attendanceService.markBulkAttendance(currentUser, classId, date, records);
    }

    @PatchMapping("/class/{classId}/student/{studentId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public AttendanceDto markSingle(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long classId,
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam AttendanceStatus status
    ) {
        return attendanceService.markSingleAttendance(currentUser, classId, studentId, date, status);
    }
}