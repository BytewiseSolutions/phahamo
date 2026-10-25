package ls.co.bytewise.api.timetable.lesson;

import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.holiday.HolidayService;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.teacher.Teacher;
import ls.co.bytewise.api.teacher.TeacherDto;
import ls.co.bytewise.api.teacher.TeacherRepository;
import ls.co.bytewise.api.timetable.Timetable;
import ls.co.bytewise.api.timetable.TimetableRepository;
import ls.co.bytewise.api.timetable.period.PeriodType;
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
    private final TeacherRepository teacherRepository;
    private final HolidayService holidayService;

    public LessonService(LessonRepository lessonRepository,
                            TimetableRepository timetableRepository,
                            TeacherRepository teacherRepository,
                            HolidayService holidayService) {
        this.lessonRepository = lessonRepository;
        this.timetableRepository = timetableRepository;
        this.teacherRepository = teacherRepository;
        this.holidayService = holidayService;
    }

    @Transactional
    public List<LessonDto> getOrCreateLessonsForClassAndDate(User requester, Long schoolClassId, LocalDate date) {
        Long schoolId = TenantContext.getSchoolId();

        if (holidayService.isHoliday(schoolId, date)) {
            return lessonRepository
                    .findAllByTimetable_Assignment_SchoolClass_IdAndDateBetweenAndSchoolIdOrderByDateAscTimetable_Period_StartTimeAsc(
                            schoolClassId, date, date, schoolId)
                    .stream().filter(this::isLesson).map(LessonDto::new).toList();
        }

        List<Timetable> slots = timetableRepository
                .findAllByAssignment_SchoolClass_IdAndSchoolId(schoolClassId, schoolId).stream()
                .filter(this::isLessonPeriod)
                .filter(t -> t.getDayOfWeek() == date.getDayOfWeek())
                .toList();

        School school = resolveActiveSchool(requester, schoolId);

        slots.forEach(slot -> lessonRepository
                .findByTimetable_IdAndDateAndSchoolId(slot.getId(), date, schoolId)
                .orElseGet(() -> createLesson(requester, school, slot, date)));

        return lessonRepository
                .findAllByTimetable_Assignment_SchoolClass_IdAndDateBetweenAndSchoolIdOrderByDateAscTimetable_Period_StartTimeAsc(
                        schoolClassId, date, date, schoolId)
                .stream().filter(this::isLesson).map(LessonDto::new).toList();
    }

    @Transactional
    public List<LessonDto> getOrCreateLessonsForTeacherAndDate(User requester, Long teacherId, LocalDate date) {
        Long schoolId = TenantContext.getSchoolId();

        if (holidayService.isHoliday(schoolId, date)) {
            return mergeTeacherLessons(
                    lessonRepository
                            .findAllByTimetable_Assignment_Teacher_IdAndDateAndSchoolIdOrderByTimetable_Period_StartTimeAsc(
                                    teacherId, date, schoolId),
                    lessonRepository
                            .findAllBySubstituteTeacher_IdAndDateAndSchoolIdOrderByTimetable_Period_StartTimeAsc(
                                    teacherId, date, schoolId));
        }

        List<Timetable> slots = timetableRepository
                .findAllByAssignment_Teacher_IdAndSchoolId(teacherId, schoolId).stream()
                .filter(this::isLessonPeriod)
                .filter(t -> t.getDayOfWeek() == date.getDayOfWeek())
                .toList();

        School school = resolveActiveSchool(requester, schoolId);

        slots.forEach(slot -> lessonRepository
                .findByTimetable_IdAndDateAndSchoolId(slot.getId(), date, schoolId)
                .orElseGet(() -> createLesson(requester, school, slot, date)));

        return mergeTeacherLessons(
                lessonRepository
                        .findAllByTimetable_Assignment_Teacher_IdAndDateAndSchoolIdOrderByTimetable_Period_StartTimeAsc(
                                teacherId, date, schoolId),
                lessonRepository
                        .findAllBySubstituteTeacher_IdAndDateAndSchoolIdOrderByTimetable_Period_StartTimeAsc(
                                teacherId, date, schoolId));
    }

    @Transactional
    public List<LessonDto> getOrCreateLessonsForClassInRange(User requester, Long schoolClassId, LocalDate startDate, LocalDate endDate) {
        validateRange(startDate, endDate);

        Long schoolId = TenantContext.getSchoolId();
        List<Timetable> slots = timetableRepository
                .findAllByAssignment_SchoolClass_IdAndSchoolId(schoolClassId, schoolId);
        slots = slots.stream().filter(this::isLessonPeriod).toList();

        School school = resolveActiveSchool(requester, schoolId);

        List<Timetable> slotsByDay = slots;
        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            for (Timetable slot : slotsByDay) {
                if (slot.getDayOfWeek() != cursor.getDayOfWeek()) continue;
                if (holidayService.isHoliday(schoolId, cursor)) continue;
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
                .filter(this::isLesson)
                .map(LessonDto::new)
                .toList();
    }

    @Transactional
    public List<LessonDto> getOrCreateLessonsForTeacherInRange(User requester, Long teacherId, LocalDate startDate, LocalDate endDate) {
        validateRange(startDate, endDate);

        Long schoolId = TenantContext.getSchoolId();
        List<Timetable> slots = timetableRepository
                .findAllByAssignment_Teacher_IdAndSchoolId(teacherId, schoolId);
        slots = slots.stream().filter(this::isLessonPeriod).toList();

        School school = resolveActiveSchool(requester, schoolId);

        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            for (Timetable slot : slots) {
                if (slot.getDayOfWeek() != cursor.getDayOfWeek()) continue;
                if (holidayService.isHoliday(schoolId, cursor)) continue;
                if (lessonRepository.existsByTimetable_IdAndDateAndSchoolId(slot.getId(), cursor, schoolId)) continue;

                try {
                    createLesson(requester, school, slot, cursor);
                } catch (org.springframework.dao.DataIntegrityViolationException ignored) {
                }
            }
            cursor = cursor.plusDays(1);
        }

        return mergeTeacherLessons(
                lessonRepository
                        .findAllByTimetable_Assignment_Teacher_IdAndDateBetweenAndSchoolIdOrderByDateAscTimetable_Period_StartTimeAsc(
                                teacherId, startDate, endDate, schoolId),
                lessonRepository
                        .findAllBySubstituteTeacher_IdAndDateBetweenAndSchoolIdOrderByDateAscTimetable_Period_StartTimeAsc(
                                teacherId, startDate, endDate, schoolId));
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

    private List<LessonDto> mergeTeacherLessons(List<Lesson> regularLessons,
                                                List<Lesson> substituteLessons) {
        java.util.Map<Long, Lesson> merged = new java.util.LinkedHashMap<>();
        regularLessons.stream().filter(this::isLesson)
                .forEach(lesson -> merged.put(lesson.getId(), lesson));
        substituteLessons.stream().filter(this::isLesson)
                .forEach(lesson -> merged.putIfAbsent(lesson.getId(), lesson));
        return merged.values().stream().map(LessonDto::new).toList();
    }

    private boolean isLessonPeriod(Timetable timetable) {
        return timetable.getPeriod().getType() == null
                || timetable.getPeriod().getType() == PeriodType.LESSON;
    }

    private boolean isLesson(Lesson lesson) {
        return isLessonPeriod(lesson.getTimetable());
    }

    @Transactional
    public LessonDto updateStatus(User editor, Long id, LessonStatus newStatus,
                                  Long substituteTeacherUserId) {
        Lesson lesson = getLessonInCurrentSchool(id);

        if (holidayService.isHoliday(TenantContext.getSchoolId(), lesson.getDate())
                && newStatus != LessonStatus.CANCELLED) {
            throw new LessonExceptions.InvalidLessonException(
                    "Lessons on a school holiday must remain cancelled");
        }

        if (newStatus == LessonStatus.SUBSTITUTED) {
            if (substituteTeacherUserId == null) {
                throw new LessonExceptions.InvalidLessonException(
                        "A substitute teacher is required for a substituted lesson");
            }

            Long schoolId = TenantContext.getSchoolId();
            Teacher substitute = teacherRepository.findByUserIdAndSchoolId(
                            substituteTeacherUserId, schoolId)
                    .orElseThrow(() -> new LessonExceptions.InvalidLessonException(
                            "Substitute teacher not found"));

            Long usualTeacherId = lesson.getTimetable().getAssignment().getTeacher() != null
                    ? lesson.getTimetable().getAssignment().getTeacher().getId() : null;
            if (substitute.getUser().getId().equals(usualTeacherId)) {
                throw new LessonExceptions.InvalidLessonException(
                        "The usual teacher cannot substitute their own lesson");
            }

            boolean recurringConflict = timetableRepository.existsOverlappingForTeacher(
                    schoolId,
                    lesson.getDate().getDayOfWeek(),
                    substituteTeacherUserId,
                    lesson.getTimetable().getPeriod().getStartTime(),
                    lesson.getTimetable().getPeriod().getEndTime(),
                    -1L);
            boolean substitutionConflict = lessonRepository.existsOverlappingSubstitution(
                    schoolId,
                    lesson.getDate(),
                    substituteTeacherUserId,
                    lesson.getTimetable().getPeriod().getStartTime(),
                    lesson.getTimetable().getPeriod().getEndTime(),
                    lesson.getId());

            if (recurringConflict || substitutionConflict) {
                throw new LessonExceptions.InvalidLessonException(
                        "This teacher is not free during the lesson period");
            }

            lesson.setSubstituteTeacher(substitute.getUser());
        } else {
            lesson.setSubstituteTeacher(null);
        }

        lesson.setStatus(newStatus);
        lesson.setUpdatedBy(editor);
        Lesson saved = lessonRepository.save(lesson);
        return new LessonDto(saved);
    }

    @Transactional(readOnly = true)
    public List<TeacherDto> getAvailableSubstituteTeachers(Long lessonId) {
        Lesson lesson = getLessonInCurrentSchool(lessonId);
        Long schoolId = TenantContext.getSchoolId();
        Long usualTeacherId = lesson.getTimetable().getAssignment().getTeacher() != null
                ? lesson.getTimetable().getAssignment().getTeacher().getId() : null;

        return teacherRepository.findAllBySchoolId(schoolId).stream()
                .filter(teacher -> teacher.getUser() != null)
                .filter(teacher -> !teacher.getUser().getId().equals(usualTeacherId))
                .filter(teacher -> !timetableRepository.existsOverlappingForTeacher(
                        schoolId,
                        lesson.getDate().getDayOfWeek(),
                        teacher.getUser().getId(),
                        lesson.getTimetable().getPeriod().getStartTime(),
                        lesson.getTimetable().getPeriod().getEndTime(),
                        -1L))
                .filter(teacher -> !lessonRepository.existsOverlappingSubstitution(
                        schoolId,
                        lesson.getDate(),
                        teacher.getUser().getId(),
                        lesson.getTimetable().getPeriod().getStartTime(),
                        lesson.getTimetable().getPeriod().getEndTime(),
                        lesson.getId()))
                .map(TeacherDto::new)
                .toList();
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
