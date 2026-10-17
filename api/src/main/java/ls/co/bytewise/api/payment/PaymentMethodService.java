package ls.co.bytewise.api.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    public List<PaymentMethodDto> listAll() {
        return paymentMethodRepository.findAllByOrderBySortOrderAsc().stream()
                .map(PaymentMethodDto::new)
                .toList();
    }

    public List<PaymentMethodDto> listActive() {
        return paymentMethodRepository.findAllByActiveTrueOrderBySortOrderAsc().stream()
                .map(PaymentMethodDto::new)
                .toList();
    }

    @Transactional
    public PaymentMethodDto create(PaymentMethodDto dto) {
        if (dto.getType() == null) {
            throw new IllegalArgumentException("Type is required");
        }
        if (dto.getLabel() == null || dto.getLabel().isBlank()) {
            throw new IllegalArgumentException("Label is required");
        }

        PaymentMethod method = new PaymentMethod();
        method.setType(dto.getType());
        method.setLabel(dto.getLabel().trim());
        method.setDetails(dto.getDetails() != null ? dto.getDetails() : "");
        method.setActive(dto.getActive() != null ? dto.getActive() : true);
        method.setSortOrder(dto.getSortOrder());

        PaymentMethod saved = paymentMethodRepository.save(method);
        return new PaymentMethodDto(saved);
    }

    @Transactional
    public PaymentMethodDto update(Long id, PaymentMethodDto dto) {
        PaymentMethod method = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found"));

        if (dto.getLabel() != null && !dto.getLabel().isBlank()) {
            method.setLabel(dto.getLabel().trim());
        }
        if (dto.getDetails() != null) {
            method.setDetails(dto.getDetails());
        }
        if (dto.getActive() != null) {
            method.setActive(dto.getActive());
        }

        method.setSortOrder(dto.getSortOrder());

        PaymentMethod saved = paymentMethodRepository.save(method);
        return new PaymentMethodDto(saved);
    }
}