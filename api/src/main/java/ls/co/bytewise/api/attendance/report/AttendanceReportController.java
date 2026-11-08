package ls.co.bytewise.api.attendance.report;

import ls.co.bytewise.api.user.User;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance/report")
public class AttendanceReportController {

    private final AttendanceReportService reportService;

    public AttendanceReportController(AttendanceReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/lessons")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public Page<LessonAttendanceSummaryDto> lessonBreakdown(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Pageable pageable) {
        return reportService.lessonBreakdownPage(classId, subjectId, from, to, safePage(pageable));
    }

    @GetMapping("/class/{classId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public Page<StudentAttendanceSummaryDto> classBreakdown(
            @PathVariable Long classId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Pageable pageable) {
        return reportService.classBreakdownPage(classId, subjectId, from, to, safePage(pageable));
    }

    @GetMapping("/class")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public Page<StudentAttendanceSummaryDto> classBreakdownAll(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Pageable pageable) {
        return reportService.classBreakdownPage(classId, subjectId, from, to, safePage(pageable));
    }

    private Pageable safePage(Pageable pageable) {
        return PageRequest.of(Math.max(pageable.getPageNumber(), 0),
                Math.min(Math.max(pageable.getPageSize(), 1), 100));
    }

    @GetMapping("/student/{studentId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public StudentAttendanceReportDto studentReport(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.studentReport(studentId, classId, subjectId, from, to);
    }

    @GetMapping("/me")
    @Secured({"ROLE_STUDENT"})
    public StudentAttendanceReportDto myReport(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) Long termId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.studentReportForUser(currentUser, termId, from, to);
    }
}
