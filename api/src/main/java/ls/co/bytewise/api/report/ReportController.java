package ls.co.bytewise.api.report;

import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.term.TermRepository;
import ls.co.bytewise.api.security.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
public class ReportController {

    private final ReportService reportService;
    private final ReportPdfRenderer reportPdfRenderer;
    private final TermRepository termRepository;

    public ReportController(ReportService reportService,
                            ReportPdfRenderer reportPdfRenderer,
                            TermRepository termRepository) {
        this.reportService = reportService;
        this.reportPdfRenderer = reportPdfRenderer;
        this.termRepository = termRepository;
    }

    @GetMapping("/student")
    public List<StudentReportRowDto> getStudentReport() {
        return reportService.getStudentReport();
    }

    @GetMapping("/attendance")
    public List<AttendanceReportRowDto> getAttendanceReport(@RequestParam Long termId) {
        return reportService.getAttendanceReport(termId);
    }

    @GetMapping("/academic")
    public List<AcademicReportRowDto> getAcademicReport(@RequestParam Long termId) {
        return reportService.getAcademicReport(termId);
    }

    @GetMapping("/financial")
    public List<FinancialReportRowDto> getFinancialReport(@RequestParam Long termId) {
        return reportService.getFinancialReport(termId);
    }

    @GetMapping("/student/pdf")
    public ResponseEntity<byte[]> studentPdf() {
        byte[] pdf = reportPdfRenderer.render("report-student", reportService.getStudentReport(), "Student Enrollment Report", "All Terms");
        return pdfResponse(pdf, "student-report.pdf");
    }

    @GetMapping("/attendance/pdf")
    public ResponseEntity<byte[]> attendancePdf(@RequestParam Long termId) {
        String termName = getTermName(termId);
        byte[] pdf = reportPdfRenderer.render("report-attendance", reportService.getAttendanceReport(termId), "Attendance Report", termName);
        return pdfResponse(pdf, "attendance-report.pdf");
    }

    @GetMapping("/academic/pdf")
    public ResponseEntity<byte[]> academicPdf(@RequestParam Long termId) {
        String termName = getTermName(termId);
        byte[] pdf = reportPdfRenderer.render("report-academic", reportService.getAcademicReport(termId), "Academic Performance Report", termName);
        return pdfResponse(pdf, "academic-report.pdf");
    }

    @GetMapping("/financial/pdf")
    public ResponseEntity<byte[]> financialPdf(@RequestParam Long termId) {
        String termName = getTermName(termId);
        byte[] pdf = reportPdfRenderer.render("report-financial", reportService.getFinancialReport(termId), "Financial Collection Report", termName);
        return pdfResponse(pdf, "financial-report.pdf");
    }

    private ResponseEntity<byte[]> pdfResponse(byte[] pdf, String filename) {
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(pdf);
    }

    private String getTermName(Long termId) {
        Long schoolId = TenantContext.getSchoolId();
        return termRepository.findByIdAndSchoolId(termId, schoolId)
                .map(Term::getName)
                .orElse("Unknown Term");
    }
}