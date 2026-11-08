package ls.co.bytewise.api.dataexport;

import ls.co.bytewise.api.common.DataFile;
import ls.co.bytewise.api.user.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/data-exports")
public class SchoolDataExportController {
    private final SchoolDataExportService service;
    private final SchoolDataExportJob job;

    public SchoolDataExportController(SchoolDataExportService service, SchoolDataExportJob job) {
        this.service = service; this.job = job;
    }

    @PostMapping
    @Secured("ROLE_SCHOOL_ADMIN")
    public ResponseEntity<SchoolDataExportResponse> request(@AuthenticationPrincipal User user) {
        SchoolDataExport export = service.request(user);
        job.generate(export.getId());
        return ResponseEntity.accepted().body(new SchoolDataExportResponse(export.getId(), export.getStatus().name(), export.getExpiresAt()));
    }

    @GetMapping("/download/{token}")
    public ResponseEntity<byte[]> download(@PathVariable String token) {
        DataFile file = service.download(token);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"school-data-export.zip\"")
                .body(file.getContents());
    }

    public record SchoolDataExportResponse(Long id, String status, Instant expiresAt) {}
}
