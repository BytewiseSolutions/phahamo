package ls.co.bytewise.api.reportcard.email;

import ls.co.bytewise.api.user.User;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report-cards/email")
public class ReportCardEmailController {

    private final ReportCardEmailService emailService;

    public ReportCardEmailController(ReportCardEmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/student/{studentId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public EmailReportCardResultDto emailStudent(
            @AuthenticationPrincipal User sender,
            @PathVariable Long studentId,
            @RequestParam Long termId) {
        return emailService.emailForStudent(studentId, termId, sender);
    }

    @PostMapping("/class/{classId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public EmailReportCardResultDto emailClass(
            @AuthenticationPrincipal User sender,
            @PathVariable Long classId,
            @RequestParam Long termId) {
        return emailService.emailForClass(classId, termId, sender);
    }

    @GetMapping("/class/{classId}/parents-without-email")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<ParentWithoutEmailDto> parentsWithoutEmail(@PathVariable Long classId) {
        return emailService.parentsWithoutEmail(classId);
    }
}