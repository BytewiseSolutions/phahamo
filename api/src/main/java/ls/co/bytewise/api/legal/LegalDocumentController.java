package ls.co.bytewise.api.legal;

import ls.co.bytewise.api.user.User;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/legal")
public class LegalDocumentController {
    private final LegalDocumentService service;

    public LegalDocumentController(LegalDocumentService service) { this.service = service; }

    @GetMapping("/terms")
    public LegalDocumentDto terms() { return service.getCurrent(LegalDocumentType.TERMS); }

    @GetMapping("/privacy")
    public LegalDocumentDto privacy() { return service.getCurrent(LegalDocumentType.PRIVACY); }

    @GetMapping("/{type}/file")
    public ResponseEntity<byte[]> file(@PathVariable String type) {
        LegalDocumentType documentType;
        try {
            documentType = LegalDocumentType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown legal document type: " + type);
        }
        LegalDocument document = service.file(documentType);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + document.getOriginalFilename() + "\"")
                .contentType(MediaType.parseMediaType(document.getMimeType()))
                .body(document.getFileData());
    }

    @GetMapping("/admin/documents")
    @Secured("ROLE_SYSTEM_ADMIN")
    public List<LegalDocumentDto> list() { return service.list(); }

    @PostMapping(value = "/admin/documents", consumes = "multipart/form-data")
    @Secured("ROLE_SYSTEM_ADMIN")
    public LegalDocumentDto publish(@RequestParam LegalDocumentType type,
                                    @RequestParam String version,
                                    @RequestParam(required = false) Instant effectiveAt,
                                    @RequestPart MultipartFile file,
                                    @AuthenticationPrincipal User currentUser) {
        return service.publish(type, version, effectiveAt, file, currentUser);
    }

    @DeleteMapping("/admin/documents/{id}")
    @Secured("ROLE_SYSTEM_ADMIN")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
