package ls.co.bytewise.api.parent;

import ls.co.bytewise.api.attendance.report.StudentAttendanceReportDto;
import ls.co.bytewise.api.result.TermResultDto;
import ls.co.bytewise.api.studentfee.StudentFeeAccountDto;
import ls.co.bytewise.api.user.User;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parent")
@Secured("ROLE_PARENT")
public class ParentPortalController {
    private final ParentPortalService parentPortalService;

    public ParentPortalController(ParentPortalService parentPortalService) {
        this.parentPortalService = parentPortalService;
    }

    @GetMapping("/children")
    public List<ParentChildDto> children(@AuthenticationPrincipal User user) {
        return parentPortalService.children(user);
    }

    @GetMapping("/dashboard")
    public ParentDashboardDto dashboard(@AuthenticationPrincipal User user) {
        return parentPortalService.dashboard(user);
    }

    @GetMapping("/children/{studentId}/attendance")
    public StudentAttendanceReportDto attendance(@AuthenticationPrincipal User user,
                                                 @PathVariable Long studentId,
                                                 @RequestParam Long termId) {
        return parentPortalService.attendance(user, studentId, termId);
    }

    @GetMapping("/children/{studentId}/results")
    public List<TermResultDto> results(@AuthenticationPrincipal User user,
                                       @PathVariable Long studentId,
                                       @RequestParam Long termId) {
        return parentPortalService.results(user, studentId, termId);
    }

    @GetMapping("/children/{studentId}/report-card")
    public ResponseEntity<byte[]> reportCard(@AuthenticationPrincipal User user,
                                             @PathVariable Long studentId,
                                             @RequestParam Long termId) {
        byte[] pdf = parentPortalService.reportCard(user, studentId, termId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"report-card-" + studentId + "-term-" + termId + ".pdf\"")
                .contentLength(pdf.length)
                .body(pdf);
    }

    @GetMapping("/children/{studentId}/fee-statement")
    public ResponseEntity<byte[]> feeStatement(@AuthenticationPrincipal User user,
                                               @PathVariable Long studentId,
                                               @RequestParam Long termId) {
        byte[] pdf = parentPortalService.feeStatement(user, studentId, termId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"fee-statement-" + studentId + "-term-" + termId + ".pdf\"")
                .contentLength(pdf.length)
                .body(pdf);
    }

    @GetMapping("/children/{studentId}/fees")
    public StudentFeeAccountDto fees(@AuthenticationPrincipal User user,
                                     @PathVariable Long studentId,
                                     @RequestParam Long termId) {
        return parentPortalService.fees(user, studentId, termId);
    }
}
