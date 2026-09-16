package ls.co.bytewise.api.timetable.period;

import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PeriodService {

    private final PeriodRepository periodRepository;

    public PeriodService(PeriodRepository periodRepository) {
        this.periodRepository = periodRepository;
    }

    @Transactional
    public PeriodDto createPeriod(User creator, PeriodDto dto) {
        Long schoolId = TenantContext.getSchoolId();

        if (!dto.getEndTime().isAfter(dto.getStartTime())) {
            throw new PeriodExceptions.InvalidPeriodException("End time must be after start time");
        }

        if (periodRepository.existsBySchoolIdAndOrderIndex(schoolId, dto.getOrderIndex())) {
            throw new PeriodExceptions.InvalidPeriodException(
                    "A period with this order already exists for your school");
        }

        School school = creator.getSchools().stream()
                .filter(s -> s.getId().equals(schoolId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Active school not found"));

        Period period = new Period();
        period.setName(dto.getName());
        period.setStartTime(dto.getStartTime());
        period.setEndTime(dto.getEndTime());
        period.setOrderIndex(dto.getOrderIndex());
        period.setSchool(school);
        period.setCreatedBy(creator);
        period.setUpdatedBy(creator);

        Period saved = periodRepository.save(period);
        return new PeriodDto(saved);
    }

    public List<PeriodDto> listPeriods() {
        Long schoolId = TenantContext.getSchoolId();
        return periodRepository.findAllBySchoolIdOrderByOrderIndexAsc(schoolId).stream()
                .map(PeriodDto::new)
                .toList();
    }

    public PeriodDto getPeriod(Long id) {
        return new PeriodDto(getPeriodInCurrentSchool(id));
    }

    @Transactional
    public PeriodDto updatePeriod(User editor, Long id, PeriodDto dto) {
        Period period = getPeriodInCurrentSchool(id);

        if (dto.getName() != null) {
            period.setName(dto.getName());
        }

        if (dto.getStartTime() != null) {
            period.setStartTime(dto.getStartTime());
        }

        if (dto.getEndTime() != null) {
            period.setEndTime(dto.getEndTime());
        }

        if (period.getEndTime() != null && period.getStartTime() != null
                && !period.getEndTime().isAfter(period.getStartTime())) {
            throw new PeriodExceptions.InvalidPeriodException("End time must be after start time");
        }

        if (dto.getOrderIndex() != null) {
            period.setOrderIndex(dto.getOrderIndex());
        }

        period.setUpdatedBy(editor);

        Period saved = periodRepository.save(period);
        return new PeriodDto(saved);
    }

    private Period getPeriodInCurrentSchool(Long id) {
        Long schoolId = TenantContext.getSchoolId();

        Period period = periodRepository.findById(id)
                .orElseThrow(() -> new PeriodExceptions.PeriodNotFoundException("Period not found"));

        if (!period.getSchool().getId().equals(schoolId)) {
            throw new PeriodExceptions.PeriodNotFoundException("Period not found");
        }

        return period;
    }
}