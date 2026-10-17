package ls.co.bytewise.api.studentfee;

import ls.co.bytewise.api.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student-fees")
@Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
public class StudentFeeController {

    private final StudentFeeService feeService;

    public StudentFeeController(StudentFeeService feeService) {
        this.feeService = feeService;
    }

    @PostMapping("/generate/{studentId}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<StudentFeeAccountDto> generateForStudent(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long studentId,
            @RequestParam Long termId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feeService.generateForStudent(studentId, termId, currentUser));
    }

    @PostMapping("/generate/class/{classId}")
    public ResponseEntity<List<StudentFeeAccountDto>> generateForClass(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long classId,
            @RequestParam Long termId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feeService.generateForClass(classId, termId, currentUser));
    }

    @GetMapping("/student/{studentId}")
    public StudentFeeAccountDto getAccount(@PathVariable Long studentId,
                                           @RequestParam Long termId) {
        return feeService.getAccount(studentId, termId);
    }

    @GetMapping("/term/{termId}")
    public List<StudentFeeAccountDto> listForTerm(@PathVariable Long termId) {
        return feeService.listForTerm(termId);
    }

    @GetMapping("/class/{classId}")
    public List<StudentFeeAccountDto> listForClass(@PathVariable Long classId,
                                                   @RequestParam Long termId) {
        return feeService.listForClass(classId, termId);
    }

    @PostMapping("/{accountId}/adjustments")
    public StudentFeeAccountDto addAdjustment(@AuthenticationPrincipal User currentUser,
                                              @PathVariable Long accountId,
                                              @RequestBody AdjustmentPayloadDto dto) {
        return feeService.addAdjustment(accountId, dto, currentUser);
    }

    @DeleteMapping("/adjustments/{adjustmentId}")
    public StudentFeeAccountDto removeAdjustment(@PathVariable Long adjustmentId) {
        return feeService.removeAdjustment(adjustmentId);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        feeService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public StudentFeeAccountDto getById(@PathVariable Long id) {
        return feeService.getById(id);
    }

    @PostMapping("/{accountId}/payments")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_FINANCE_OFFICER"})
    public StudentFeeAccountDto recordPayment(@AuthenticationPrincipal User currentUser,
                                              @PathVariable Long accountId,
                                              @RequestBody PaymentPayloadDto dto) {
        return feeService.recordPayment(accountId, dto, currentUser);
    }

    @GetMapping("/me")
    @Secured({"ROLE_STUDENT"})
    public StudentFeeAccountDto getMyAccount(@AuthenticationPrincipal User currentUser,
                                             @RequestParam Long termId) {
        return feeService.getAccountForStudent(currentUser, termId);
    }

    @GetMapping("/payments/{paymentId}/receipt")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_FINANCE_OFFICER", "ROLE_STUDENT"})
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long paymentId,
                                                  @AuthenticationPrincipal User currentUser) {
        byte[] pdf = feeService.generateReceipt(paymentId, currentUser);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"receipt-" + paymentId + ".pdf\"")
                .body(pdf);
    }
}