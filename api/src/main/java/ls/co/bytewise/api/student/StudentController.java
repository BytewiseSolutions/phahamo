package ls.co.bytewise.api.student;

import jakarta.validation.Valid;
import ls.co.bytewise.api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.io.IOException;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public Page<StudentDto> list(@RequestParam(required = false) String search, Pageable pageable) {
        return studentService.listStudents(search, pageable);
    }

    @GetMapping("/class/{schoolClassId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<StudentDto> listByClass(@PathVariable Long schoolClassId) {
        return studentService.listStudentsByClass(schoolClassId);
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public StudentDto get(@PathVariable Long id) {
        return studentService.getStudent(id);
    }

    @GetMapping("/{id}/photo")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        var photo = studentService.getPhoto(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + photo.getOriginalFileName() + "\"")
                .contentType(MediaType.parseMediaType(photo.getContentType()))
                .body(photo.getContents());
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public StudentDto uploadPhoto(@AuthenticationPrincipal User currentUser,
                                  @PathVariable Long id,
                                  @RequestParam("file") org.springframework.web.multipart.MultipartFile file)
            throws IOException {
        return studentService.uploadPhoto(currentUser, id, file);
    }

    @DeleteMapping("/{id}/photo")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public ResponseEntity<Void> removePhoto(@AuthenticationPrincipal User currentUser,
                                            @PathVariable Long id) {
        studentService.removePhoto(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public StudentDto update(@AuthenticationPrincipal User currentUser,
                             @PathVariable Long id,
                             @Valid @RequestBody StudentDto dto) {
        return studentService.updateStudent(currentUser, id, dto);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/class")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public StudentDto assignClass(@AuthenticationPrincipal User currentUser,
                                  @PathVariable Long id,
                             @RequestBody StudentClassUpdateDto dto) {
        return studentService.assignClass(currentUser, id, dto);
    }

    @GetMapping("/{id}/class-fee-preview")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public StudentClassFeePreviewDto previewClassChange(@PathVariable Long id,
                                                        @RequestParam Long schoolClassId) {
        return studentService.previewClassChange(id, schoolClassId);
    }

    @PutMapping("/{id}/reactivate")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public StudentDto reactivate(@AuthenticationPrincipal User currentUser,
                                 @PathVariable Long id) {
        return studentService.reactivate(currentUser, id);
    }
}
