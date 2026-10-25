package ls.co.bytewise.api.timetable;

import ls.co.bytewise.api.audit.AuditAction;
import ls.co.bytewise.api.audit.AuditLogService;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.subject.subjectclassassignment.SubjectClassAssignment;
import ls.co.bytewise.api.subject.subjectclassassignment.SubjectClassAssignmentRepository;
import ls.co.bytewise.api.timetable.period.Period;
import ls.co.bytewise.api.timetable.period.PeriodExceptions;
import ls.co.bytewise.api.timetable.period.PeriodRepository;
import ls.co.bytewise.api.timetable.period.PeriodType;
import ls.co.bytewise.api.timetable.lesson.Lesson;
import ls.co.bytewise.api.timetable.lesson.LessonRepository;
import ls.co.bytewise.api.holiday.HolidayService;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.time.LocalDate;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

@Service
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final PeriodRepository periodRepository;
    private final SubjectClassAssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;
    private final LessonRepository lessonRepository;
    private final HolidayService holidayService;
    private final SchoolRepository schoolRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final AuditLogService auditLogService;

    public TimetableService(TimetableRepository timetableRepository,
                            PeriodRepository periodRepository,
                            SubjectClassAssignmentRepository assignmentRepository,
                            StudentRepository studentRepository,
                            LessonRepository lessonRepository,
                            HolidayService holidayService,
                            SchoolRepository schoolRepository,
                            SchoolClassRepository schoolClassRepository,
                            AuditLogService auditLogService) {
        this.timetableRepository = timetableRepository;
        this.periodRepository = periodRepository;
        this.assignmentRepository = assignmentRepository;
        this.studentRepository = studentRepository;
        this.lessonRepository = lessonRepository;
        this.holidayService = holidayService;
        this.schoolRepository = schoolRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public TimetableDto createTimetable(User creator, TimetableDto dto) {
        Long schoolId = TenantContext.getSchoolId();

        Period period = periodRepository.findById(dto.getPeriodId())
                .orElseThrow(() -> new PeriodExceptions.PeriodNotFoundException("Period not found"));

        ensureLessonPeriod(period);

        SubjectClassAssignment assignment = assignmentRepository.findById(dto.getAssignmentId())
                .orElseThrow(() -> new TimetableExceptions.TimetableNotFoundException("Assignment not found"));

        if (assignment.getTeacher() == null) {
            throw new TimetableExceptions.TimetableConflictException(
                    "This subject-class assignment has no teacher assigned yet");
        }

        validateDailyAssignmentLimit(schoolId, dto.getDayOfWeek(), assignment, -1L);

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
        auditLogService.record(school, creator, AuditAction.TIMETABLE_CREATED,
                "TIMETABLE", saved.getId(), describe(saved));
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

        ensureLessonPeriod(newPeriod);

        SubjectClassAssignment newAssignment = timetable.getAssignment();
        if (dto.getAssignmentId() != null) {
            newAssignment = assignmentRepository.findById(dto.getAssignmentId())
                    .orElseThrow(() -> new TimetableExceptions.TimetableNotFoundException("Assignment not found"));
        }

        if (newAssignment.getTeacher() == null) {
            throw new TimetableExceptions.TimetableConflictException(
                    "This subject-class assignment has no teacher assigned yet");
        }

        validateDailyAssignmentLimit(schoolId, newDayOfWeek, newAssignment, timetable.getId());

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
        auditLogService.record(saved.getSchool(), editor, AuditAction.TIMETABLE_UPDATED,
                "TIMETABLE", saved.getId(), describe(saved));
        return new TimetableDto(saved);
    }

    private void validateDailyAssignmentLimit(Long schoolId,
                                              DayOfWeek dayOfWeek,
                                              SubjectClassAssignment assignment,
                                              Long excludeId) {
        long existingSlots = timetableRepository.countSlotsForAssignmentOnDay(
                schoolId, dayOfWeek, assignment.getId(), excludeId);

        if (existingSlots >= 2) {
            throw new TimetableExceptions.TimetableConflictException(
                    "This subject can be scheduled at most twice per day for this class");
        }
    }

    private boolean isSameSlot(Timetable existing, DayOfWeek dayOfWeek, Period period, SubjectClassAssignment assignment) {
        return existing.getDayOfWeek() == dayOfWeek
                && existing.getPeriod().getId().equals(period.getId())
                && existing.getAssignment().getId().equals(assignment.getId());
    }

    public List<TimetableDto> listAll() {
        return listAll(null);
    }

    public List<TimetableDto> listAll(LocalDate date) {
        Long schoolId = TenantContext.getSchoolId();
        return timetableRepository.findAllBySchoolId(schoolId).stream()
                .map(slot -> {
                    Lesson lesson = date == null ? null : lessonRepository
                            .findByTimetable_IdAndDateAndSchoolId(slot.getId(), date, schoolId)
                            .orElse(null);
                    return new TimetableDto(slot, lesson,
                            date == null ? null : holidayService.findForDate(schoolId, date));
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public TimetablePublicationDto publication() {
        School school = currentSchool();
        return publicationDto(school, publicationProblems(school.getId()));
    }

    @Transactional
    public TimetablePublicationDto publish(User publisher, boolean confirmProblems) {
        School school = currentSchool();
        List<String> problems = publicationProblems(school.getId());
        if (!problems.isEmpty() && !confirmProblems) {
            return publicationDto(school, problems);
        }

        school.setTimetablePublished(true);
        school.setTimetablePublishedAt(Instant.now());
        school.setTimetablePublishedBy(publisher);
        School saved = schoolRepository.save(school);
        auditLogService.record(saved, publisher, AuditAction.TIMETABLE_PUBLISHED,
                "SCHOOL", saved.getId(), "Published the timetable"
                        + (problems.isEmpty() ? "" : " with " + problems.size() + " acknowledged problem(s)"));
        return publicationDto(saved, problems);
    }

    private TimetablePublicationDto publicationDto(School school, List<String> problems) {
        TimetablePublicationDto dto = new TimetablePublicationDto();
        dto.setPublished(school.isTimetablePublished());
        dto.setPublishedAt(school.getTimetablePublishedAt());
        dto.setProblems(problems);
        return dto;
    }

    private List<String> publicationProblems(Long schoolId) {
        List<Timetable> slots = timetableRepository.findAllBySchoolId(schoolId);
        List<String> problems = new ArrayList<>();
        Set<DayOfWeek> schoolDays = Set.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

        for (SchoolClass schoolClass : schoolClassRepository.findAllBySchool_Id(schoolId)) {
            for (DayOfWeek day : schoolDays) {
                boolean hasLesson = slots.stream().anyMatch(slot ->
                        slot.getAssignment().getSchoolClass().getId().equals(schoolClass.getId())
                                && slot.getDayOfWeek() == day
                                && isLessonPeriod(slot.getPeriod()));
                if (!hasLesson) {
                    problems.add(schoolClass.getName() + " has no lessons on " + day);
                }
            }
        }

        Map<String, Long> assignmentDayCounts = new HashMap<>();
        for (Timetable slot : slots) {
            if (!isLessonPeriod(slot.getPeriod())) continue;
            String key = slot.getAssignment().getId() + ":" + slot.getDayOfWeek();
            assignmentDayCounts.merge(key, 1L, Long::sum);
            if (assignmentDayCounts.get(key) == 3L) {
                problems.add(slot.getAssignment().getSubject().getName() + " for "
                        + slot.getAssignment().getSchoolClass().getName() + " has more than two periods on "
                        + slot.getDayOfWeek());
            }
        }

        for (int i = 0; i < slots.size(); i++) {
            Timetable left = slots.get(i);
            if (!isLessonPeriod(left.getPeriod())) continue;
            for (int j = i + 1; j < slots.size(); j++) {
                Timetable right = slots.get(j);
                if (!isLessonPeriod(right.getPeriod()) || left.getDayOfWeek() != right.getDayOfWeek()) continue;
                boolean overlaps = left.getPeriod().getStartTime().isBefore(right.getPeriod().getEndTime())
                        && right.getPeriod().getStartTime().isBefore(left.getPeriod().getEndTime());
                if (!overlaps) continue;
                if (left.getAssignment().getSchoolClass().getId().equals(right.getAssignment().getSchoolClass().getId())) {
                    problems.add("Class " + left.getAssignment().getSchoolClass().getName()
                            + " has overlapping lessons on " + left.getDayOfWeek());
                }
                if (left.getAssignment().getTeacher() != null && right.getAssignment().getTeacher() != null
                        && left.getAssignment().getTeacher().getId().equals(right.getAssignment().getTeacher().getId())) {
                    problems.add("Teacher " + left.getAssignment().getTeacher().getFullName()
                            + " has overlapping lessons on " + left.getDayOfWeek());
                }
            }
        }
        return problems.stream().distinct().toList();
    }

    private School currentSchool() {
        return schoolRepository.findById(TenantContext.getSchoolId())
                .orElseThrow(() -> new TimetableExceptions.TimetableNotFoundException("School not found"));
    }

    private boolean canSeeDraft(User requester) {
        return requester.hasRole(UserRole.SCHOOL_ADMIN)
                || requester.hasRole(UserRole.SYSTEM_ADMIN)
                || requester.hasRole(UserRole.PRINCIPAL);
    }

    private boolean isLessonPeriod(Period period) {
        return period.getType() == null || period.getType() == PeriodType.LESSON;
    }

    public List<TimetableDto> listByTeacher(Long teacherId) {
        return listByTeacher(null, teacherId, null);
    }

    public List<TimetableDto> listByTeacher(User requester, Long teacherId, LocalDate date) {
        Long schoolId = TenantContext.getSchoolId();
        if (!currentSchool().isTimetablePublished() && (requester == null || !canSeeDraft(requester))) {
            return List.of();
        }
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
            result.put(slot.getId(), new TimetableDto(slot, lesson,
                    holidayService.findForDate(schoolId, date)));
        }

        for (Lesson lesson : lessonRepository
                .findAllBySubstituteTeacher_IdAndDateAndSchoolIdOrderByTimetable_Period_StartTimeAsc(
                        teacherId, date, schoolId)) {
            result.putIfAbsent(lesson.getTimetable().getId(),
                    new TimetableDto(lesson.getTimetable(), lesson,
                            holidayService.findForDate(schoolId, date)));
        }

        return result.values().stream()
                .sorted(java.util.Comparator.comparing(TimetableDto::getPeriodStartTime))
                .toList();
    }

    public List<TimetableDto> listByClass(User requester, Long schoolClassId) {
        Long schoolId = TenantContext.getSchoolId();
        if (!currentSchool().isTimetablePublished() && (requester == null || !canSeeDraft(requester))) {
            return List.of();
        }
        return timetableRepository.findAllByAssignment_SchoolClass_IdAndSchoolId(schoolClassId, schoolId).stream()
                .map(TimetableDto::new)
                .toList();
    }

    public TimetableDto getTimetable(Long id) {
        return new TimetableDto(getTimetableInCurrentSchool(id));
    }

    @Transactional
    public void deleteTimetable(User deleter, Long id) {
        Timetable timetable = getTimetableInCurrentSchool(id);
        timetableRepository.delete(timetable);
        auditLogService.record(timetable.getSchool(), deleter, AuditAction.TIMETABLE_DELETED,
                "TIMETABLE", timetable.getId(), "Deleted " + describe(timetable));
    }

    private String describe(Timetable timetable) {
        return timetable.getAssignment().getSchoolClass().getName() + " - "
                + timetable.getAssignment().getSubject().getName() + " on "
                + timetable.getDayOfWeek() + " during " + timetable.getPeriod().getName();
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

    private void ensureLessonPeriod(Period period) {
        if (period.getType() != null && period.getType() != PeriodType.LESSON) {
            throw new TimetableExceptions.TimetableConflictException(
                    "Lessons cannot be added to a break or lunch period");
        }
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

        return listByClass(user, student.getCurrentClass().getId());
    }
}
