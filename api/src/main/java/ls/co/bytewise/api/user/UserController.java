package ls.co.bytewise.api.user;

import jakarta.validation.Valid;
import ls.co.bytewise.api.common.Status;
import ls.co.bytewise.api.common.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<UserDto> listUsers() {
        return userService.listUsers();
    }

    @GetMapping("/page")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public Page<UserDto> pageUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        return userService.pageUsers(search, role, status,
                PageRequest.of(Math.max(page, 0), safeSize));
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
                              @RequestBody UserDto dto) {
        return userService.updateUser(currentUser, id, dto);
    }

    @PutMapping("/me")
    public UserDto updateMe(@AuthenticationPrincipal User currentUser,
                            @RequestBody UserDto dto) {
        return userService.updateSelf(currentUser, dto);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public ResponseEntity<Void> deactivateUser(@AuthenticationPrincipal User currentUser,
                                               @PathVariable Long id) {
        userService.deactivateUser(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public ResponseEntity<Void> activateUser(@AuthenticationPrincipal User currentUser,
                                             @PathVariable Long id) {
        userService.activateUser(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/unlock")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN"})
    public ResponseEntity<Void> unlockUser(@AuthenticationPrincipal User currentUser,
                                           @PathVariable Long id) {
        userService.unlockUser(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }
}
