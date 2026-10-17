package ls.co.bytewise.api.subscription.plan;

import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.school.SchoolExceptions;
import ls.co.bytewise.api.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PlanPriceService {

    private final PlanPriceRepository planPriceRepository;
    private final SchoolRepository schoolRepository;

    public PlanPriceService(PlanPriceRepository planPriceRepository,
                            SchoolRepository schoolRepository) {
        this.planPriceRepository = planPriceRepository;
        this.schoolRepository = schoolRepository;
    }

    public List<PlanPriceDto> listAll() {
        return planPriceRepository.findAllByOrderByCurrencyAscPlanAsc().stream()
                .map(PlanPriceDto::new)
                .toList();
    }

    public List<PlanPriceDto> listForCurrentSchool() {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) {
            throw new IllegalStateException("No active school context");
        }

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new SchoolExceptions.SchoolNotFoundException("School not found"));

        return planPriceRepository.findAllByCurrencyOrderByPlanAsc(school.getCurrency()).stream()
                .filter(PlanPrice::isActive)
                .map(PlanPriceDto::new)
                .toList();
    }

    @Transactional
    public PlanPriceDto create(PlanPriceDto dto) {
        if (dto.getPlan() == null || dto.getCurrency() == null) {
            throw new IllegalArgumentException("Plan and currency are required");
        }

        if (planPriceRepository.findByPlanAndCurrency(dto.getPlan(), dto.getCurrency()).isPresent()) {
            throw new IllegalArgumentException("A price for this plan and currency already exists");
        }

        PlanPrice price = new PlanPrice();
        price.setPlan(dto.getPlan());
        price.setCurrency(dto.getCurrency());
        price.setAmount(dto.getAmount() != null ? dto.getAmount() : BigDecimal.ZERO);
        price.setActive(dto.getActive() != null ? dto.getActive() : true);

        PlanPrice saved = planPriceRepository.save(price);
        return new PlanPriceDto(saved);
    }

    @Transactional
    public PlanPriceDto update(Long id, BigDecimal amount, Boolean active) {
        PlanPrice price = planPriceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan price not found"));

        if (amount != null) {
            if (amount.signum() < 0) {
                throw new IllegalArgumentException("Amount cannot be negative");
            }
            price.setAmount(amount);
        }

        if (active != null) {
            price.setActive(active);
        }

        PlanPrice saved = planPriceRepository.save(price);
        return new PlanPriceDto(saved);
    }
}