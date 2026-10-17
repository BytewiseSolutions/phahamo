package ls.co.bytewise.api.reportcard;

import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.user.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/report-cards")
public class ReportCardController {

    private final ReportCardService reportCardService;

    public ReportCardController(ReportCardService reportCardService) {
        this.reportCardService = reportCardService;
    }

    @GetMapping("/student/{studentId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public ResponseEntity<byte[]> forStudent(@PathVariable Long studentId,
                                             @RequestParam Long termId,
                                             @AuthenticationPrincipal User currentUser) {
        byte[] pdf = reportCardService.renderPdf(studentId, termId, currentUser);
        return pdfResponse(pdf, "report-card-" + studentId + ".pdf");
    }

    @GetMapping("/me")
    @Secured({"ROLE_STUDENT"})
    public ResponseEntity<byte[]> forMe(@RequestParam Long termId,
                                        @AuthenticationPrincipal User currentUser) {
        byte[] pdf = reportCardService.renderPdfForUser(currentUser, termId);
        return pdfResponse(pdf, "report-card.pdf");
    }

    @GetMapping("/class/{classId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public ResponseEntity<byte[]> forClass(@PathVariable Long classId,
                                           @RequestParam Long termId,
                                           @AuthenticationPrincipal User currentUser) throws Exception {
        reportCardService.requireClass(classId); // tenant check
        List<Student> students = reportCardService.listStudentsForClass(classId);

        ByteArrayOutputStream zipBytes = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(zipBytes)) {
            for (Student s : students) {
                byte[] pdf = reportCardService.renderPdf(s.getId(), termId, currentUser);
                String filename = safeFilename(s) + ".pdf";
                zip.putNextEntry(new ZipEntry(filename));
                zip.write(pdf);
                zip.closeEntry();
            }
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/zip")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"report-cards-class-" + classId + ".zip\"")
                .body(zipBytes.toByteArray());
    }

    private ResponseEntity<byte[]> pdfResponse(byte[] pdf, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentLength(pdf.length)
                .body(pdf);
    }

    private String safeFilename(Student s) {
        String name = s.getUser().getLastName() + "-" + s.getUser().getFirstName();
        return name.replaceAll("[^a-zA-Z0-9-_]", "_")
                + "-" + s.getAdmissionNumber();
    }
}