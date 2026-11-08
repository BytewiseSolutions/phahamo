package ls.co.bytewise.api.result;

import ls.co.bytewise.api.user.User;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/term-results")
public class TermResultController {

    private final TermResultService termResultService;

    public TermResultController(TermResultService termResultService) {
        this.termResultService = termResultService;
    }

    @GetMapping
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public Page<TermResultDto> list(@RequestParam Long classId,
                                    @RequestParam Long subjectId,
                                    @RequestParam Long termId,
                                    Pageable pageable) {
        return termResultService.pageFor(classId, subjectId, termId,
                PageRequest.of(Math.max(pageable.getPageNumber(), 0),
                        Math.min(Math.max(pageable.getPageSize(), 1), 100)));
    }

    @GetMapping("/class")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<TermResultDto> listForClass(@RequestParam Long classId,
                                            @RequestParam Long termId) {
        return termResultService.listForClassAndTerm(classId, termId);
    }

    @GetMapping("/student")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<TermResultDto> listForStudent(@RequestParam Long studentId,
                                              @RequestParam Long termId) {
        return termResultService.listForStudentAndTerm(studentId, termId);
    }

    @GetMapping("/{id}/audit")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<ResultAuditDto> audit(@PathVariable Long id) {
        return termResultService.listAudit(id);
    }

    @PostMapping("/calculate")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<TermResultDto> calculate(@AuthenticationPrincipal User currentUser,
                                         @RequestParam Long classId,
                                         @RequestParam Long subjectId,
                                         @RequestParam Long termId) {
        return termResultService.calculate(currentUser, classId, subjectId, termId);
    }

    @PostMapping("/finalise")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER"})
    public List<TermResultDto> finalise(@AuthenticationPrincipal User currentUser,
                                        @RequestParam Long classId,
                                        @RequestParam Long subjectId,
                                        @RequestParam Long termId) {
        return termResultService.finalise(currentUser, classId, subjectId, termId);
    }

    @PostMapping("/reopen")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<TermResultDto> reopen(@AuthenticationPrincipal User currentUser,
                                      @RequestParam Long classId,
                                      @RequestParam Long subjectId,
                                      @RequestParam Long termId,
                                      @RequestParam String reason) {
        return termResultService.reopen(currentUser, classId, subjectId, termId, reason);
    }

    @PostMapping("/{id}/override")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public TermResultDto override(@AuthenticationPrincipal User currentUser,
                                  @PathVariable Long id,
                                  @RequestParam Double mark,
                                  @RequestParam String reason) {
        return termResultService.overrideMark(currentUser, id, mark, reason);
    }

    @GetMapping("/me")
    @Secured({"ROLE_STUDENT"})
    public StudentResultsReportDto myResults(
            @AuthenticationPrincipal User currentUser,
            @RequestParam Long termId) {
        return termResultService.myResults(currentUser, termId);
    }
}
