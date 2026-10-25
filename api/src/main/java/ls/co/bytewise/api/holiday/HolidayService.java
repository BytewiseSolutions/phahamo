package ls.co.bytewise.api.holiday;

import jakarta.transaction.Transactional;
import ls.co.bytewise.api.audit.AuditAction;
import ls.co.bytewise.api.audit.AuditLogService;
import ls.co.bytewise.api.attendance.AttendanceRepository;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.timetable.lesson.Lesson;
import ls.co.bytewise.api.timetable.lesson.LessonRepository;
import ls.co.bytewise.api.timetable.lesson.LessonStatus;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.DateTimeException;
import java.util.List;
import java.time.temporal.ChronoUnit;

@Service
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final SchoolRepository schoolRepository;
    private final LessonRepository lessonRepository;
    private final AttendanceRepository attendanceRepository;
    private final AuditLogService auditLogService;

    public HolidayService(HolidayRepository holidayRepository,
                          SchoolRepository schoolRepository,
                          LessonRepository lessonRepository,
                          AttendanceRepository attendanceRepository,
                          AuditLogService auditLogService) {
        this.holidayRepository = holidayRepository;
        this.schoolRepository = schoolRepository;
        this.lessonRepository = lessonRepository;
        this.attendanceRepository = attendanceRepository;
        this.auditLogService = auditLogService;
    }

    public List<HolidayDto> list(LocalDate from, LocalDate to) {
        Long schoolId = TenantContext.getSchoolId();
        List<Holiday> holidays;
        if (from != null && to != null) {
            holidays = holidayRepository.findAllBySchoolIdOrderByStartDateAsc(schoolId).stream()
                    .filter(holiday -> overlaps(holiday, from, to))
                    .toList();
        } else {
            holidays = holidayRepository.findAllBySchoolIdOrderByStartDateAsc(schoolId);
        }
        return holidays.stream().map(HolidayDto::new).toList();
    }

    public HolidayDto get(Long id) {
        return new HolidayDto(findOrThrow(id));
    }

    @Transactional
    public HolidayDto create(User creator, HolidayDto dto) {
        validateDates(dto);
        Long schoolId = TenantContext.getSchoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new HolidayExceptions.InvalidHolidayException("Active school not found"));

        Holiday holiday = new Holiday();
        holiday.setName(dto.getName().trim());
        holiday.setStartDate(dto.getStartDate());
        holiday.setEndDate(dto.getEndDate());
        holiday.setRecurring(dto.isRecurring());
        holiday.setSchool(school);
        holiday.setCreatedBy(creator);
        holiday.setUpdatedBy(creator);

        Holiday saved = holidayRepository.save(holiday);
        cancelLessonsAndAttendance(creator, schoolId, saved.getStartDate(), saved.getEndDate());
        auditLogService.record(school, creator, AuditAction.HOLIDAY_CREATED,
                "HOLIDAY", saved.getId(), "Created holiday " + saved.getName()
                        + " from " + saved.getStartDate() + " to " + saved.getEndDate());
        return new HolidayDto(saved);
    }

    @Transactional
    public HolidayDto update(User editor, Long id, HolidayDto dto) {
        validateDates(dto);
        Holiday holiday = findOrThrow(id);
        holiday.setName(dto.getName().trim());
        holiday.setStartDate(dto.getStartDate());
        holiday.setEndDate(dto.getEndDate());
        holiday.setRecurring(dto.isRecurring());
        holiday.setUpdatedBy(editor);
        Holiday saved = holidayRepository.save(holiday);
        cancelLessonsAndAttendance(editor, saved.getSchool().getId(), saved.getStartDate(), saved.getEndDate());
        auditLogService.record(saved.getSchool(), editor, AuditAction.HOLIDAY_UPDATED,
                "HOLIDAY", saved.getId(), "Updated holiday " + saved.getName()
                        + " from " + saved.getStartDate() + " to " + saved.getEndDate());
        return new HolidayDto(saved);
    }

    @Transactional
    public void delete(User deleter, Long id) {
        Holiday holiday = findOrThrow(id);
        holidayRepository.delete(holiday);
        auditLogService.record(holiday.getSchool(), deleter, AuditAction.HOLIDAY_DELETED,
                "HOLIDAY", holiday.getId(), "Deleted holiday " + holiday.getName());
    }

    public boolean isHoliday(Long schoolId, LocalDate date) {
        return holidayRepository.findAllBySchoolIdOrderByStartDateAsc(schoolId).stream()
                .anyMatch(holiday -> appliesOn(holiday, date));
    }

    public List<HolidayDto> findForDate(Long schoolId, LocalDate date) {
        return holidayRepository.findAllBySchoolIdOrderByStartDateAsc(schoolId).stream()
                .filter(holiday -> appliesOn(holiday, date))
                .map(HolidayDto::new).toList();
    }

    private boolean appliesOn(Holiday holiday, LocalDate date) {
        if (!holiday.isRecurring()) {
            return !date.isBefore(holiday.getStartDate()) && !date.isAfter(holiday.getEndDate());
        }

        long duration = ChronoUnit.DAYS.between(holiday.getStartDate(), holiday.getEndDate()) + 1L;
        for (int year = date.getYear() - 1; year <= date.getYear(); year++) {
            LocalDate occurrenceStart = recurringDate(holiday.getStartDate(), year);
            if (occurrenceStart == null) continue;
            LocalDate occurrenceEnd = occurrenceStart.plusDays(duration - 1);
            if (!date.isBefore(occurrenceStart) && !date.isAfter(occurrenceEnd)) return true;
        }
        return false;
    }

    private boolean overlaps(Holiday holiday, LocalDate from, LocalDate to) {
        if (!holiday.isRecurring()) {
            return !holiday.getStartDate().isAfter(to) && !holiday.getEndDate().isBefore(from);
        }

        for (int year = from.getYear() - 1; year <= to.getYear(); year++) {
            LocalDate occurrenceStart = recurringDate(holiday.getStartDate(), year);
            if (occurrenceStart == null) continue;
            long duration = ChronoUnit.DAYS.between(holiday.getStartDate(), holiday.getEndDate()) + 1L;
            LocalDate occurrenceEnd = occurrenceStart.plusDays(duration - 1);
            if (!occurrenceStart.isAfter(to) && !occurrenceEnd.isBefore(from)) return true;
        }
        return false;
    }

    private LocalDate recurringDate(LocalDate source, int year) {
        try {
            return LocalDate.of(year, source.getMonth(), source.getDayOfMonth());
        } catch (DateTimeException ignored) {
            return null;
        }
    }

    private void cancelLessonsAndAttendance(User editor, Long schoolId,
                                            LocalDate fromDate, LocalDate toDate) {
        List<Lesson> lessons = lessonRepository
                .findAllBySchoolIdAndDateBetween(schoolId, fromDate, toDate);
        if (lessons.isEmpty()) return;

        attendanceRepository.deleteAllByLessonIdIn(lessons.stream().map(Lesson::getId).toList());
        lessons.forEach(lesson -> {
            lesson.setStatus(LessonStatus.CANCELLED);
            lesson.setSubstituteTeacher(null);
            lesson.setUpdatedBy(editor);
        });
        lessonRepository.saveAll(lessons);
    }

    private Holiday findOrThrow(Long id) {
        return holidayRepository.findByIdAndSchoolId(id, TenantContext.getSchoolId())
                .orElseThrow(() -> new HolidayExceptions.HolidayNotFoundException("Holiday not found"));
    }

    private void validateDates(HolidayDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new HolidayExceptions.InvalidHolidayException("Holiday name is required");
        }
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new HolidayExceptions.InvalidHolidayException("Start date and end date are required");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new HolidayExceptions.InvalidHolidayException("End date must be on or after start date");
        }
    }
}
