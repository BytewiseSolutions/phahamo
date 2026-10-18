package ls.co.bytewise.api.timetable.lesson;

import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.timetable.Timetable;
import ls.co.bytewise.api.timetable.TimetableRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class LessonService {

    private static final long MAX_RANGE_DAYS = 366;

    private final LessonRepository lessonRepository;
    private final TimetableRepository timetableRepository;

    public LessonService(LessonRepository lessonRepository, TimetableRepository timetableRepository) {
        this.lessonRepository = lessonRepository;
        this.timetableRepository = timetableRepository;
    }

    @Transactional
    public List<LessonDto> getOrCreateLessonsForClassAndDate(User requester, Long schoolClassId, LocalDate date) {
        Long schoolId = TenantContext.getSchoolId();

        List<Timetable> slots = timetableRepository
                .findAllByAssignment_SchoolClass_IdAndSchoolId(schoolClassId, schoolId).stream()
                .filter(t -> t.getDayOfWeek() == date.getDayOfWeek())
                .toList();

        School school = resolveActiveSchool(requester, schoolId);

        return slots.stream()
                .map(slot -> lessonRepository
                        .findByTimetable_IdAndDateAndSchoolId(slot.getId(), date, schoolId)
                        .orElseGet(() -> createLesson(requester, school, slot, date)))
                .map(LessonDto::new)
                .toList();
    }

    @Transactional
    public List<LessonDto> getOrCreateLessonsForTeacherAndDate(User requester, Long teacherId, LocalDate date) {
        Long schoolId = TenantContext.getSchoolId();

        List<Timetable> slots = timetableRepository
                .findAllByAssignment_Teacher_IdAndSchoolId(teacherId, schoolId).stream()
                .filter(t -> t.getDayOfWeek() == date.getDayOfWeek())
                .toList();

        School school = resolveActiveSchool(requester, schoolId);

        return slots.stream()
                .map(slot -> lessonRepository
                        .findByTimetable_IdAndDateAndSchoolId(slot.getId(), date, schoolId)
                        .orElseGet(() -> createLesson(requester, school, slot, date)))
                .map(LessonDto::new)
                .toList();
    }

    @Transactional
    public List<LessonDto> getOrCreateLessonsForClassInRange(User requester, Long schoolClassId, LocalDate startDate, LocalDate endDate) {
        validateRange(startDate, endDate);

        Long schoolId = TenantContext.getSchoolId();
        List<Timetable> slots = timetableRepository
                .findAllByAssignment_SchoolClass_IdAndSchoolId(schoolClassId, schoolId);

        School school = resolveActiveSchool(requester, schoolId);

        List<Timetable> slotsByDay = slots;
        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            for (Timetable slot : slotsByDay) {
                if (slot.getDayOfWeek() != cursor.getDayOfWeek()) continue;
                if (lessonRepository.existsByTimetable_IdAndDateAndSchoolId(slot.getId(), cursor, schoolId)) continue;

                try {
                    createLesson(requester, school, slot, cursor);
                } catch (org.springframework.dao.DataIntegrityViolationException ignored) {
                    // Race condition: another request created it first. Ignore.
                }
            }
            cursor = cursor.plusDays(1);
        }

        return lessonRepository
                .findAllByTimetable_Assignment_SchoolClass_IdAndDateBetweenAndSchoolIdOrderByDateAscTimetable_Period_StartTimeAsc(
                        schoolClassId, startDate, endDate, schoolId)
                .stream()
                .map(LessonDto::new)
                .toList();
    }

    @Transactional
    public List<LessonDto> getOrCreateLessonsForTeacherInRange(User requester, Long teacherId, LocalDate startDate, LocalDate endDate) {
        validateRange(startDate, endDate);

        Long schoolId = TenantContext.getSchoolId();
        List<Timetable> slots = timetableRepository
                .findAllByAssignment_Teacher_IdAndSchoolId(teacherId, schoolId);

        School school = resolveActiveSchool(requester, schoolId);

        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            for (Timetable slot : slots) {
                if (slot.getDayOfWeek() != cursor.getDayOfWeek()) continue;
                if (lessonRepository.existsByTimetable_IdAndDateAndSchoolId(slot.getId(), cursor, schoolId)) continue;

                try {
                    createLesson(requester, school, slot, cursor);
                } catch (org.springframework.dao.DataIntegrityViolationException ignored) {
                }
            }
            cursor = cursor.plusDays(1);
        }

        return lessonRepository
                .findAllByTimetable_Assignment_Teacher_IdAndDateBetweenAndSchoolIdOrderByDateAscTimetable_Period_StartTimeAsc(
                        teacherId, startDate, endDate, schoolId)
                .stream()
                .map(LessonDto::new)
                .toList();
    }

    private void validateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new LessonExceptions.InvalidLessonException("Start date and end date are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new LessonExceptions.InvalidLessonException("End date must be on or after start date");
        }
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days > MAX_RANGE_DAYS) {
            throw new LessonExceptions.InvalidLessonException(
                    "Date range cannot exceed " + MAX_RANGE_DAYS + " days. Selected range is " + days + " days."
            );
        }
    }

    private Lesson createLesson(User creator, School school, Timetable slot, LocalDate date) {
        Lesson lesson = new Lesson();
        lesson.setTimetable(slot);
        lesson.setDate(date);
        lesson.setStatus(LessonStatus.SCHEDULED);
        lesson.setSchool(school);
        lesson.setCreatedBy(creator);
        lesson.setUpdatedBy(creator);
        return lessonRepository.save(lesson);
    }

    public LessonDto getLesson(Long id) {
        return new LessonDto(getLessonInCurrentSchool(id));
    }

    @Transactional
    public LessonDto updateStatus(User editor, Long id, LessonStatus newStatus) {
        Lesson lesson = getLessonInCurrentSchool(id);
        lesson.setStatus(newStatus);
        lesson.setUpdatedBy(editor);
        Lesson saved = lessonRepository.save(lesson);
        return new LessonDto(saved);
    }

    private Lesson getLessonInCurrentSchool(Long id) {
        Long schoolId = TenantContext.getSchoolId();

        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new LessonExceptions.LessonNotFoundException("Lesson not found"));

        if (!lesson.getSchool().getId().equals(schoolId)) {
            throw new LessonExceptions.LessonNotFoundException("Lesson not found");
        }

        return lesson;
    }

    private School resolveActiveSchool(User requester, Long schoolId) {
        return requester.getSchools().stream()
                .filter(s -> s.getId().equals(schoolId))
                .findFirst()
                .orElseThrow(() -> new LessonExceptions.ActiveSchoolNotFoundException("Active school not found"));
    }
}