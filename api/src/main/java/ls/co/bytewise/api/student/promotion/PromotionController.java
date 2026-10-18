package ls.co.bytewise.api.student.promotion;

import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.StudentDto;
import ls.co.bytewise.api.user.User;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promotions")
@Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
public class PromotionController {

    private final PromotionService service;

    public PromotionController(PromotionService service) {
        this.service = service;
    }

    @GetMapping("/preview")
    public List<StudentDto> preview(@RequestParam Long fromClassId) {
        return service.preview(fromClassId);
    }

    @PostMapping
    public Map<String, Object> promote(@AuthenticationPrincipal User currentUser,
                                       @RequestBody PromotionPayloadDto request) {
        String batchId = service.promote(currentUser, request);
        return Map.of("batchId", batchId);
    }

    @GetMapping("/history")
    public List<PromotionDto> history(
            @RequestParam(required = false) Long studentId) {
        Long schoolId = TenantContext.getSchoolId();
        if (studentId != null) {
            return service.historyForStudent(schoolId, studentId);
        }
        return service.historyForSchool(schoolId);
    }
}