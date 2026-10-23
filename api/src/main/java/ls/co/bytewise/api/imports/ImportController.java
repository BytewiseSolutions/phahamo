package ls.co.bytewise.api.imports;

import ls.co.bytewise.api.user.User;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final StudentImportService studentImportService;
    private final TeacherImportService teacherImportService;

    public ImportController(StudentImportService studentImportService,
                            TeacherImportService teacherImportService) {
        this.studentImportService = studentImportService;
        this.teacherImportService = teacherImportService;
    }

    @GetMapping("/templates/{type}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public ResponseEntity<Resource> downloadTemplate(@PathVariable String type) {
        String filename = switch (type) {
            case "students" -> "students-template.xlsx";
            case "teachers" -> "teachers-template.xlsx";
            default -> throw new ImportExceptions.InvalidFileException("Unknown template type: " + type);
        };

        Resource resource = new ClassPathResource("templates/import/" + filename);
        if (!resource.exists()) {
            throw new ImportExceptions.InvalidFileException("Template not found: " + filename);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    @PostMapping("/students")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public ImportResultDto importStudents(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dryRun", defaultValue = "true") boolean dryRun) {
        return studentImportService.process(currentUser, file, dryRun);
    }

    @PostMapping("/teachers")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public ImportResultDto importTeachers(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dryRun", defaultValue = "true") boolean dryRun) {
        return teacherImportService.process(currentUser, file, dryRun);
    }
}