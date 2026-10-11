package ls.co.bytewise.api.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final DataFileRepository dataFileRepository;

    public FileController(DataFileRepository dataFileRepository) {
        this.dataFileRepository = dataFileRepository;
    }

    @PostMapping
    @Secured({
            "ROLE_SCHOOL_ADMIN",
            "ROLE_PRINCIPAL",
            "ROLE_FINANCE_OFFICER",
            "ROLE_SYSTEM_ADMIN"
    })
    public ResponseEntity<FileDto> upload(@RequestParam("file") MultipartFile file) throws IOException {
        DataFile dataFile = DataFile.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .contents(file.getBytes())
                .fileSize(file.getSize())
                .build();

        DataFile saved = dataFileRepository.save(dataFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(new FileDto(saved));
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_PRINCIPAL", "ROLE_FINANCE_OFFICER", "ROLE_SYSTEM_ADMIN"})
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        DataFile file = dataFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.parseMediaType(file.getType() != null ? file.getType() : "application/octet-stream"))
                .body(file.getContents());
    }
}