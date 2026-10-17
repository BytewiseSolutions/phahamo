package ls.co.bytewise.api.subscription.plan;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/plan-prices")
public class PlanPriceController {

    private final PlanPriceService planPriceService;

    public PlanPriceController(PlanPriceService planPriceService) {
        this.planPriceService = planPriceService;
    }

    @GetMapping
    @Secured("ROLE_SYSTEM_ADMIN")
    public List<PlanPriceDto> listAll() {
        return planPriceService.listAll();
    }

    @GetMapping("/me")
    @Secured({
            "ROLE_SCHOOL_ADMIN",
            "ROLE_PRINCIPAL",
            "ROLE_FINANCE_OFFICER"
    })
    public List<PlanPriceDto> listForCurrentSchool() {
        return planPriceService.listForCurrentSchool();
    }

    @PostMapping
    @Secured("ROLE_SYSTEM_ADMIN")
    public ResponseEntity<PlanPriceDto> create(@RequestBody PlanPriceDto dto) {
        PlanPriceDto created = planPriceService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_SYSTEM_ADMIN")
    public PlanPriceDto update(@PathVariable Long id,
                               @RequestBody PlanPriceDto dto) {
        return planPriceService.update(id, dto.getAmount(), dto.getActive());
    }
}