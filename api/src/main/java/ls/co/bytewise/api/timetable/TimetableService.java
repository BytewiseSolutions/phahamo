package ls.co.bytewise.api.timetable;

import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.subject.subjectclassassignment.SubjectClassAssignment;
import ls.co.bytewise.api.subject.subjectclassassignment.SubjectClassAssignmentRepository;
import ls.co.bytewise.api.timetable.period.Period;
import ls.co.bytewise.api.timetable.period.PeriodExceptions;
import ls.co.bytewise.api.timetable.period.PeriodRepository;
import ls.co.bytewise.api.timetable.lesson.Lesson;
import ls.co.bytewise.api.timetable.lesson.LessonRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final PeriodRepository periodRepository;
    private final SubjectClassAssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;
    private final LessonRepository lessonRepository;

    public TimetableService(TimetableRepository timetableRepository,
                            PeriodRepository periodRepository,
                            SubjectClassAssignmentRepository assignmentRepository,
                            StudentRepository studentRepository,
                            LessonRepository lessonRepository) {
        this.timetableRepository = timetableRepository;
        this.periodRepository = periodRepository;
        this.assignmentRepository = assignmentRepository;
        this.studentRepository = studentRepository;
        this.lessonRepository = lessonRepository;
    }

    @Transactional
    public TimetableDto createTimetable(User creator, TimetableDto dto) {
        Long schoolId = TenantContext.getSchoolId();

        Period period = periodRepository.findById(dto.getPeriodId())
                .orElseThrow(() -> new PeriodExceptions.PeriodNotFoundException("Period not found"));

        SubjectClassAssignment assignment = assignmentRepository.findById(dto.getAssignmentId())
                .orElseThrow(() -> new TimetableExceptions.TimetableNotFoundException("Assignment not found"));

        if (assignment.getTeacher() == null) {
            throw new TimetableExceptions.TimetableConflictException(
                    "This subject-class assignment has no teacher assigned yet");
        }

        boolean teacherBooked = timetableRepository.existsOverlappingForTeacher(
                schoolId, dto.getDayOfWeek(), assignment.getTeacher().getId(),
                period.getStartTime(), period.getEndTime(), -1L);

        if (teacherBooked) {
            throw new TimetableExceptions.TimetableConflictException(
                    "This teacher is already booked for an overlapping time on this day");
        }

        boolean classBooked = timetableRepository.existsOverlappingForClass(
                schoolId, dto.getDayOfWeek(), assignment.getSchoolClass().getId(),
                period.getStartTime(), period.getEndTime(), -1L);

        if (classBooked) {
            throw new TimetableExceptions.TimetableConflictException(
                    "This class already has an overlapping lesson scheduled on this day");
        }

        School school = creator.getSchools().stream()
                .filter(s -> s.getId().equals(schoolId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Active school not found"));

        Timetable timetable = new Timetable();
        timetable.setDayOfWeek(dto.getDayOfWeek());
        timetable.setPeriod(period);
        timetable.setAssignment(assignment);
        timetable.setSchool(school);
        timetable.setCreatedBy(creator);
        timetable.setUpdatedBy(creator);

        Timetable saved = timetableRepository.save(timetable);
        return new TimetableDto(saved);
    }

    @Transactional
    public TimetableDto updateTimetable(User editor, Long id, TimetableDto dto) {
        Timetable timetable = getTimetableInCurrentSchool(id);
        Long schoolId = TenantContext.getSchoolId();

        DayOfWeek newDayOfWeek = dto.getDayOfWeek() != null ? dto.getDayOfWeek() : timetable.getDayOfWeek();

        Period newPeriod = timetable.getPeriod();
        if (dto.getPeriodId() != null) {
            newPeriod = periodRepository.findById(dto.getPeriodId())
                    .orElseThrow(() -> new PeriodExceptions.PeriodNotFoundException("Period not found"));
        }

        SubjectClassAssignment newAssignment = timetable.getAssignment();
        if (dto.getAssignmentId() != null) {
            newAssignment = assignmentRepository.findById(dto.getAssignmentId())
                    .orElseThrow(() -> new TimetableExceptions.TimetableNotFoundException("Assignment not found"));
        }

        if (newAssignment.getTeacher() == null) {
            throw new TimetableExceptions.TimetableConflictException(
                    "This subject-class assignment has no teacher assigned yet");
        }

        boolean teacherBooked = timetableRepository.existsOverlappingForTeacher(
                schoolId, newDayOfWeek, newAssignment.getTeacher().getId(),
                newPeriod.getStartTime(), newPeriod.getEndTime(), timetable.getId());

        if (teacherBooked) {
            throw new TimetableExceptions.TimetableConflictException(
                    "This teacher is already booked for an overlapping time on this day");
        }

        boolean classBooked = timetableRepository.existsOverlappingForClass(
                schoolId, newDayOfWeek, newAssignment.getSchoolClass().getId(),
                newPeriod.getStartTime(), newPeriod.getEndTime(), timetable.getId());

        if (classBooked) {
            throw new TimetableExceptions.TimetableConflictException(
                    "This class already has an overlapping lesson scheduled on this day");
        }

        timetable.setDayOfWeek(newDayOfWeek);
        timetable.setPeriod(newPeriod);
        timetable.setAssignment(newAssignment);
        timetable.setUpdatedBy(editor);

        Timetable saved = timetableRepository.save(timetable);
        return new TimetableDto(saved);
    }

    private boolean isSameSlot(Timetable existing, DayOfWeek dayOfWeek, Period period, SubjectClassAssignment assignment) {
        return existing.getDayOfWeek() == dayOfWeek
                && existing.getPeriod().getId().equals(period.getId())
                && existing.getAssignment().getId().equals(assignment.getId());
    }

    public List<TimetableDto> listAll() {
        Long schoolId = TenantContext.getSchoolId();
        return timetableRepository.findAllBySchoolId(schoolId).stream()
                .map(TimetableDto::new)
                .toList();
    }

    public List<TimetableDto> listByTeacher(Long teacherId) {
        return listByTeacher(teacherId, null);
    }

    public List<TimetableDto> listByTeacher(Long teacherId, LocalDate date) {
        Long schoolId = TenantContext.getSchoolId();
        List<Timetable> regularSlots = timetableRepository
                .findAllByAssignment_Teacher_IdAndSchoolId(teacherId, schoolId);

        if (date == null) {
            return regularSlots.stream().map(TimetableDto::new).toList();
        }

        Map<Long, TimetableDto> result = new LinkedHashMap<>();
        for (Timetable slot : regularSlots.stream()
                .filter(slot -> slot.getDayOfWeek() == date.getDayOfWeek())
                .toList()) {
            Lesson lesson = lessonRepository
                    .findByTimetable_IdAndDateAndSchoolId(slot.getId(), date, schoolId)
                    .orElse(null);
            result.put(slot.getId(), new TimetableDto(slot, lesson));
        }

        for (Lesson lesson : lessonRepository
                .findAllBySubstituteTeacher_IdAndDateAndSchoolIdOrderByTimetable_Period_StartTimeAsc(
                        teacherId, date, schoolId)) {
            result.putIfAbsent(lesson.getTimetable().getId(),
                    new TimetableDto(lesson.getTimetable(), lesson));
        }

        return result.values().stream()
                .sorted(java.util.Comparator.comparing(TimetableDto::getPeriodStartTime))
                .toList();
    }

    public List<TimetableDto> listByClass(Long schoolClassId) {
        Long schoolId = TenantContext.getSchoolId();
        return timetableRepository.findAllByAssignment_SchoolClass_IdAndSchoolId(schoolClassId, schoolId).stream()
                .map(TimetableDto::new)
                .toList();
    }

    public TimetableDto getTimetable(Long id) {
        return new TimetableDto(getTimetableInCurrentSchool(id));
    }

    @Transactional
    public void deleteTimetable(Long id) {
        Timetable timetable = getTimetableInCurrentSchool(id);
        timetableRepository.delete(timetable);
    }

    private Timetable getTimetableInCurrentSchool(Long id) {
        Long schoolId = TenantContext.getSchoolId();

        Timetable timetable = timetableRepository.findById(id)
                .orElseThrow(() -> new TimetableExceptions.TimetableNotFoundException("Timetable not found"));

        if (!timetable.getSchool().getId().equals(schoolId)) {
            throw new TimetableExceptions.TimetableNotFoundException("Timetable not found");
        }

        return timetable;
    }

    public List<TimetableDto> listForStudent(User user) {
        Long schoolId = TenantContext.getSchoolId();

        Student student = studentRepository.findByUserId(user.getId())
                .filter(s -> s.getSchool().getId().equals(schoolId))
                .orElseThrow(() -> new TimetableExceptions.TimetableNotFoundException(
                        "No student record linked to this account"));

        if (student.getCurrentClass() == null) {
            throw new TimetableExceptions.TimetableNotFoundException(
                    "Your class has not been assigned yet. Please contact the school administrator.");
        }

        return listByClass(student.getCurrentClass().getId());
    }
}
