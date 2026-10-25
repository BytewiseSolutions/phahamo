package ls.co.bytewise.api.fee;

import ls.co.bytewise.api.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FeeStructureController {

    private final FeeStructureService feeService;

    public FeeStructureController(FeeStructureService feeService) {
        this.feeService = feeService;
    }

    @GetMapping("/fee-structures")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<FeeStructureDto> listStructures(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long termId) {
        return feeService.listStructures(classId, termId);
    }

    @GetMapping("/fee-structures/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public FeeStructureDto getStructure(@PathVariable Long id) {
        return feeService.getStructure(id);
    }

    @PostMapping("/fee-structures")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<FeeStructureDto> createStructure(
            @AuthenticationPrincipal User currentUser,
            @RequestBody FeeStructureDto dto) {
        FeeStructureDto created = feeService.createStructure(currentUser, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/fee-structures/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public FeeStructureDto updateStructure(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @RequestBody FeeStructureDto dto) {
        return feeService.updateStructure(currentUser, id, dto);
    }

    @DeleteMapping("/fee-structures/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<Void> deleteStructure(@AuthenticationPrincipal User currentUser,
                                                @PathVariable Long id) {
        feeService.deleteStructure(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/annual-fees")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public List<AnnualFeeDto> listAnnualFees(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long academicYearId) {
        return feeService.listAnnualFees(classId, academicYearId);
    }

    @GetMapping("/annual-fees/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public AnnualFeeDto getAnnualFee(@PathVariable Long id) {
        return feeService.getAnnualFee(id);
    }

    @PostMapping("/annual-fees")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<AnnualFeeDto> createAnnualFee(
            @AuthenticationPrincipal User currentUser,
            @RequestBody AnnualFeeDto dto) {
        AnnualFeeDto created = feeService.createAnnualFee(currentUser, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/annual-fees/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public AnnualFeeDto updateAnnualFee(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @RequestBody AnnualFeeDto dto) {
        return feeService.updateAnnualFee(currentUser, id, dto);
    }

    @DeleteMapping("/annual-fees/{id}")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<Void> deleteAnnualFee(@AuthenticationPrincipal User currentUser,
                                                @PathVariable Long id) {
        feeService.deleteAnnualFee(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/annual-fees/bulk")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ResponseEntity<List<AnnualFeeDto>> createBulk(
            @AuthenticationPrincipal User currentUser,
            @RequestBody AnnualFeeBulkDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feeService.createAnnualFeeBulk(currentUser, dto));
    }
}
