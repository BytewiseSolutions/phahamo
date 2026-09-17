package ls.co.bytewise.api.timetable.lesson;

import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.timetable.Timetable;
import ls.co.bytewise.api.timetable.TimetableRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LessonService {

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

        School school = requester.getSchools().stream()
                .filter(s -> s.getId().equals(schoolId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Active school not found"));

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

        School school = requester.getSchools().stream()
                .filter(s -> s.getId().equals(schoolId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Active school not found"));

        return slots.stream()
                .map(slot -> lessonRepository
                        .findByTimetable_IdAndDateAndSchoolId(slot.getId(), date, schoolId)
                        .orElseGet(() -> createLesson(requester, school, slot, date)))
                .map(LessonDto::new)
                .toList();
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

    public List<LessonDto> listForTeacherInRange(Long teacherId, LocalDate startDate, LocalDate endDate) {
        Long schoolId = TenantContext.getSchoolId();
        return lessonRepository
                .findAllByTimetable_Assignment_Teacher_IdAndDateBetweenAndSchoolIdOrderByDateAscTimetable_Period_StartTimeAsc(
                        teacherId, startDate, endDate, schoolId)
                .stream()
                .map(LessonDto::new)
                .toList();
    }

    public List<LessonDto> listForClassInRange(Long schoolClassId, LocalDate startDate, LocalDate endDate) {
        Long schoolId = TenantContext.getSchoolId();
        return lessonRepository
                .findAllByTimetable_Assignment_SchoolClass_IdAndDateBetweenAndSchoolIdOrderByDateAscTimetable_Period_StartTimeAsc(
                        schoolClassId, startDate, endDate, schoolId)
                .stream()
                .map(LessonDto::new)
                .toList();
    }
}