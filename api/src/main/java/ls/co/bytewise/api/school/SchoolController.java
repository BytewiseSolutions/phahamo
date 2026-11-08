package ls.co.bytewise.api.school;

import jakarta.validation.Valid;
import ls.co.bytewise.api.common.DataFile;
import ls.co.bytewise.api.user.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/schools")
public class SchoolController {

    private final SchoolService schoolService;

    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @PostMapping("/register")
    public ResponseEntity<SchoolDto> registerSchool(@Valid @RequestBody UserDto admin) {
        School savedSchool = schoolService.registerSchool(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SchoolDto(savedSchool));
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_PRINCIPAL", "ROLE_SYSTEM_ADMIN"})
    public ResponseEntity<SchoolDto> getSchool(@PathVariable Long id) {
        return ResponseEntity.ok(schoolService.getSchoolProfile(id));
    }

    @PutMapping("/{id}")
    @Secured("ROLE_SCHOOL_ADMIN")
    public ResponseEntity<SchoolDto> updateSchool(@PathVariable Long id, @Valid @RequestBody SchoolDto dto) {
        return ResponseEntity.ok(schoolService.updateSchoolProfile(id, dto));
    }

    @PostMapping(value = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured("ROLE_SCHOOL_ADMIN")
    public ResponseEntity<SchoolDto> uploadLogo(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(schoolService.updateSchoolLogo(id, file));
    }

    @GetMapping("/{id}/logo")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_PRINCIPAL", "ROLE_SYSTEM_ADMIN"})
    public ResponseEntity<byte[]> getLogo(@PathVariable Long id) {
        DataFile logo = schoolService.getSchoolLogoFile(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(logo.getContentType()))
                .body(logo.getContents());
    }
}
