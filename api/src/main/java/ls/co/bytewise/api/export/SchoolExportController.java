package ls.co.bytewise.api.export;

import ls.co.bytewise.api.user.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exports")
@Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_TEACHER"})
public class SchoolExportController {
    private final SchoolExportService service;
    public SchoolExportController(SchoolExportService service) { this.service = service; }

    @GetMapping("/classes/{classId}/students.pdf")
    public ResponseEntity<byte[]> classPdf(@AuthenticationPrincipal User user, @PathVariable Long classId) { return file(service.classList(user, classId, true), "class-list.pdf", MediaType.APPLICATION_PDF); }
    @GetMapping("/classes/{classId}/students.xlsx")
    public ResponseEntity<byte[]> classExcel(@AuthenticationPrincipal User user, @PathVariable Long classId) { return file(service.classList(user, classId, false), "class-list.xlsx", MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")); }
    @GetMapping("/timetables/classes/{classId}.pdf")
    public ResponseEntity<byte[]> classTimetable(@AuthenticationPrincipal User user, @PathVariable Long classId) { return file(service.classTimetable(user, classId), "class-timetable.pdf", MediaType.APPLICATION_PDF); }
    @GetMapping("/timetables/teachers/{teacherId}.pdf")
    public ResponseEntity<byte[]> teacherTimetable(@AuthenticationPrincipal User user, @PathVariable Long teacherId) { return file(service.teacherTimetable(user, teacherId), "teacher-timetable.pdf", MediaType.APPLICATION_PDF); }

    private ResponseEntity<byte[]> file(byte[] body, String name, MediaType type) {
        return ResponseEntity.ok().contentType(type).header("Content-Disposition", "attachment; filename=\"" + name + "\"").body(body);
    }
}
