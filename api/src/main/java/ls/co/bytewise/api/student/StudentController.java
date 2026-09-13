package ls.co.bytewise.api.student;

import jakarta.validation.Valid;
import ls.co.bytewise.api.student.status.AssignClassRequest;
import ls.co.bytewise.api.student.status.ChangeStatusRequest;
import ls.co.bytewise.api.student.status.StudentStatusChangeDto;
import ls.co.bytewise.api.user.User;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<StudentDto> create(@AuthenticationPrincipal User currentUser,
                                             @Valid @RequestBody StudentDto dto) {
        StudentDto created = studentService.createStudent(currentUser, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<StudentDto> list() {
        return studentService.listStudents();
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
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
                                  @Valid @RequestBody AssignClassRequest request) {
        return studentService.assignClass(currentUser, id, request.getSchoolClassId());
    }

    @PutMapping("/{id}/status")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public StudentDto changeStatus(@AuthenticationPrincipal User currentUser,
                                   @PathVariable Long id,
                                   @Valid @RequestBody ChangeStatusRequest request) {
        return studentService.changeStatus(currentUser, id, request.getStatus(), request.getReason());
    }

    @GetMapping("/{id}/status-history")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<StudentStatusChangeDto> getStatusHistory(@PathVariable Long id) {
        return studentService.getStatusHistory(id);
    }

    @GetMapping("/class/{schoolClassId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<StudentDto> listByClass(@PathVariable Long schoolClassId) {
        return studentService.listStudentsByClass(schoolClassId);
    }
}