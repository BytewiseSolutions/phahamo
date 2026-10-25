package ls.co.bytewise.api.holiday;

import jakarta.transaction.Transactional;
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
import java.util.List;

@Service
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final SchoolRepository schoolRepository;
    private final LessonRepository lessonRepository;
    private final AttendanceRepository attendanceRepository;

    public HolidayService(HolidayRepository holidayRepository,
                          SchoolRepository schoolRepository,
                          LessonRepository lessonRepository,
                          AttendanceRepository attendanceRepository) {
        this.holidayRepository = holidayRepository;
        this.schoolRepository = schoolRepository;
        this.lessonRepository = lessonRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public List<HolidayDto> list(LocalDate from, LocalDate to) {
        Long schoolId = TenantContext.getSchoolId();
        List<Holiday> holidays;
        if (from != null && to != null) {
            holidays = holidayRepository.findOverlapping(schoolId, from, to);
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
        holiday.setSchool(school);
        holiday.setCreatedBy(creator);
        holiday.setUpdatedBy(creator);

        Holiday saved = holidayRepository.save(holiday);
        cancelLessonsAndAttendance(creator, schoolId, saved.getStartDate(), saved.getEndDate());
        return new HolidayDto(saved);
    }

    @Transactional
    public HolidayDto update(User editor, Long id, HolidayDto dto) {
        validateDates(dto);
        Holiday holiday = findOrThrow(id);
        holiday.setName(dto.getName().trim());
        holiday.setStartDate(dto.getStartDate());
        holiday.setEndDate(dto.getEndDate());
        holiday.setUpdatedBy(editor);
        Holiday saved = holidayRepository.save(holiday);
        cancelLessonsAndAttendance(editor, saved.getSchool().getId(), saved.getStartDate(), saved.getEndDate());
        return new HolidayDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        holidayRepository.delete(findOrThrow(id));
    }

    public boolean isHoliday(Long schoolId, LocalDate date) {
        return !holidayRepository.findBySchoolIdAndDate(schoolId, date).isEmpty();
    }

    public List<HolidayDto> findForDate(Long schoolId, LocalDate date) {
        return holidayRepository.findBySchoolIdAndDate(schoolId, date)
                .stream().map(HolidayDto::new).toList();
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
