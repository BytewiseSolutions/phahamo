package ls.co.bytewise.api.timetable.period;

import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.timetable.Timetable;
import ls.co.bytewise.api.timetable.TimetableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PeriodService {

    private final PeriodRepository periodRepository;
    private final TimetableRepository timetableRepository;

    public PeriodService(PeriodRepository periodRepository, TimetableRepository timetableRepository) {
        this.periodRepository = periodRepository;
        this.timetableRepository = timetableRepository;
    }

    @Transactional
    public PeriodDto createPeriod(User creator, PeriodDto dto) {
        Long schoolId = TenantContext.getSchoolId();

        if (!dto.getEndTime().isAfter(dto.getStartTime())) {
            throw new PeriodExceptions.InvalidPeriodException("End time must be after start time");
        }

        School school = creator.getSchools().stream()
                .filter(s -> s.getId().equals(schoolId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Active school not found"));

        Period period = new Period();
        period.setName(dto.getName());
        period.setType(dto.getType() != null ? dto.getType() : PeriodType.LESSON);
        period.setStartTime(dto.getStartTime());
        period.setEndTime(dto.getEndTime());
        period.setSchool(school);
        period.setCreatedBy(creator);
        period.setUpdatedBy(creator);

        Period saved = periodRepository.save(period);
        return new PeriodDto(saved);
    }

    public List<PeriodDto> listPeriods() {
        Long schoolId = TenantContext.getSchoolId();
        return periodRepository.findAllBySchoolIdOrderByStartTimeAsc(schoolId).stream()
                .map(PeriodDto::new)
                .toList();
    }

    @Transactional
    public PeriodDto updatePeriod(User editor, Long id, PeriodDto dto) {
        Period period = getPeriodInCurrentSchool(id);

        if (dto.getName() != null) {
            period.setName(dto.getName());
        }

        if (dto.getType() != null && dto.getType() != period.getType()) {
            List<Timetable> slots = timetableRepository.findAllByPeriodIdAndSchoolId(id, TenantContext.getSchoolId());
            if (!slots.isEmpty() && dto.getType() != PeriodType.LESSON) {
                throw new PeriodExceptions.InvalidPeriodException(
                        "Remove timetable slots before changing this period to a break or lunch period");
            }
            period.setType(dto.getType());
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

        period.setUpdatedBy(editor);

        Period saved = periodRepository.save(period);
        return new PeriodDto(saved);
    }

    public PeriodDto getPeriod(Long id) {
        return new PeriodDto(getPeriodInCurrentSchool(id));
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
