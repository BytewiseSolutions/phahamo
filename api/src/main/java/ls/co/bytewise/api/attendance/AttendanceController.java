package ls.co.bytewise.api.attendance;

import jakarta.validation.Valid;
import ls.co.bytewise.api.user.User;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/lesson/{lessonId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<AttendanceDto> getAttendanceForLesson(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long lessonId
    ) {
        return attendanceService.getAttendanceForLesson(currentUser, lessonId);
    }

    @PostMapping("/lesson/{lessonId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<AttendanceDto> markBulkForLesson(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long lessonId,
            @Valid @RequestBody List<AttendanceDto> records
    ) {
        return attendanceService.markBulkAttendanceForLesson(currentUser, lessonId, records);
    }

    @PatchMapping("/lesson/{lessonId}/student/{studentId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public AttendanceDto markSingleForLesson(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long lessonId,
            @PathVariable Long studentId,
            @RequestParam AttendanceStatus status
    ) {
        return attendanceService.markSingleAttendanceForLesson(currentUser, lessonId, studentId, status);
    }
}