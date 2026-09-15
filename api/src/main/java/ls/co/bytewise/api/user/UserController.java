package ls.co.bytewise.api.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal User currentUser) {
        return new UserDto(currentUser);
    }

    @GetMapping("/admin-only-test")
    @Secured("ROLE_SCHOOL_ADMIN")
    public String adminOnlyTest() {
        return "You are a School Admin — role check passed.";
    }

    @PostMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public ResponseEntity<UserDto> createUser(@AuthenticationPrincipal User currentUser,
                                              @Valid @RequestBody UserDto dto) {
        UserDto created = userService.createUser(currentUser, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public List<UserDto> listUsers() {
        return userService.listUsers();
    }

    @GetMapping("/teachers")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<UserDto> listTeachers() {
        return userService.listTeachers();
    }

    @GetMapping("/parents")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<UserDto> listParents() {
        return userService.listParents();
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public UserDto updateUser(@AuthenticationPrincipal User currentUser,
                              @PathVariable Long id,
                              @Valid @RequestBody UserDto dto) {
        return userService.updateUser(currentUser, id, dto);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }
}