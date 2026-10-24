package ls.co.bytewise.api.student;

import jakarta.validation.Valid;
import ls.co.bytewise.api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return studentService.assignClass(currentUser, id, dto.getSchoolClassId());
    }

    @PutMapping("/{id}/reactivate")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public StudentDto reactivate(@AuthenticationPrincipal User currentUser,
                                 @PathVariable Long id) {
        return studentService.reactivate(currentUser, id);
    }
}
