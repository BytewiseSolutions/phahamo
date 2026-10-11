package ls.co.bytewise.api.school.subscription.payment;

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
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @GetMapping
    @Secured("ROLE_SYSTEM_ADMIN")
    public List<PaymentMethodDto> listAll() {
        return paymentMethodService.listAll();
    }

    @GetMapping("/active")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_PRINCIPAL", "ROLE_FINANCE_OFFICER"})
    public List<PaymentMethodDto> listActive() {
        return paymentMethodService.listActive();
    }

    @PostMapping
    @Secured("ROLE_SYSTEM_ADMIN")
    public ResponseEntity<PaymentMethodDto> create(@RequestBody PaymentMethodDto dto) {
        PaymentMethodDto created = paymentMethodService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_SYSTEM_ADMIN")
    public PaymentMethodDto update(@PathVariable Long id, @RequestBody PaymentMethodDto dto) {
        return paymentMethodService.update(id, dto);
    }
}