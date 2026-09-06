package ls.co.bytewise.api.school;

import jakarta.validation.Valid;
import ls.co.bytewise.api.user.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}