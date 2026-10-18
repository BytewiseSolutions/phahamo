package ls.co.bytewise.api.announcement;

import ls.co.bytewise.api.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService service;

    public AnnouncementController(AnnouncementService service) {
        this.service = service;
    }

    @GetMapping("/feed")
    public List<AnnouncementDto> getFeed(@AuthenticationPrincipal User currentUser) {
        return service.getFeedForUser(currentUser);
    }

    @GetMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<AnnouncementDto> listAll() {
        return service.listForAdmin();
    }

    @PostMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<AnnouncementDto> create(@AuthenticationPrincipal User currentUser,
                                                  @RequestBody AnnouncementPayloadDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(currentUser, dto));
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public AnnouncementDto update(@AuthenticationPrincipal User currentUser,
                                  @PathVariable Long id,
                                  @RequestBody AnnouncementPayloadDto dto) {
        return service.update(currentUser, id, dto);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}