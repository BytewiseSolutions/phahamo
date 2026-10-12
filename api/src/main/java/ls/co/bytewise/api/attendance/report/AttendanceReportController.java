package ls.co.bytewise.api.attendance.report;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public List<LessonAttendanceSummaryDto> lessonBreakdown(
            @RequestParam Long classId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.lessonBreakdown(classId, subjectId, from, to);
    }

    @GetMapping("/class/{classId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<StudentAttendanceSummaryDto> classBreakdown(
            @PathVariable Long classId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.classBreakdown(classId, subjectId, from, to);
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
}