package ls.co.bytewise.api.timetable;

import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.subject.subjectclassassignment.SubjectClassAssignment;
import ls.co.bytewise.api.subject.subjectclassassignment.SubjectClassAssignmentRepository;
import ls.co.bytewise.api.timetable.period.Period;
import ls.co.bytewise.api.timetable.period.PeriodExceptions;
import ls.co.bytewise.api.timetable.period.PeriodRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

@Service
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final PeriodRepository periodRepository;
    private final SubjectClassAssignmentRepository assignmentRepository;

    public TimetableService(TimetableRepository timetableRepository,
                            PeriodRepository periodRepository,
                            SubjectClassAssignmentRepository assignmentRepository) {
        this.timetableRepository = timetableRepository;
        this.periodRepository = periodRepository;
        this.assignmentRepository = assignmentRepository;
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

        boolean teacherBooked = timetableRepository
                .existsByDayOfWeekAndPeriod_IdAndAssignment_Teacher_IdAndSchoolId(
                        dto.getDayOfWeek(), period.getId(), assignment.getTeacher().getId(), schoolId);

        if (teacherBooked) {
            throw new TimetableExceptions.TimetableConflictException(
                    "This teacher is already booked for this day and period");
        }

        boolean classBooked = timetableRepository
                .existsByDayOfWeekAndPeriod_IdAndAssignment_SchoolClass_IdAndSchoolId(
                        dto.getDayOfWeek(), period.getId(), assignment.getSchoolClass().getId(), schoolId);

        if (classBooked) {
            throw new TimetableExceptions.TimetableConflictException(
                    "This class already has a lesson scheduled for this day and period");
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

        boolean teacherBooked = timetableRepository
                .existsByDayOfWeekAndPeriod_IdAndAssignment_Teacher_IdAndSchoolId(
                        newDayOfWeek, newPeriod.getId(), newAssignment.getTeacher().getId(), schoolId)
                && !isSameSlot(timetable, newDayOfWeek, newPeriod, newAssignment);

        if (teacherBooked) {
            throw new TimetableExceptions.TimetableConflictException(
                    "This teacher is already booked for this day and period");
        }

        boolean classBooked = timetableRepository
                .existsByDayOfWeekAndPeriod_IdAndAssignment_SchoolClass_IdAndSchoolId(
                        newDayOfWeek, newPeriod.getId(), newAssignment.getSchoolClass().getId(), schoolId)
                && !isSameSlot(timetable, newDayOfWeek, newPeriod, newAssignment);

        if (classBooked) {
            throw new TimetableExceptions.TimetableConflictException(
                    "This class already has a lesson scheduled for this day and period");
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
        Long schoolId = TenantContext.getSchoolId();
        return timetableRepository.findAllByAssignment_Teacher_IdAndSchoolId(teacherId, schoolId).stream()
                .map(TimetableDto::new)
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
}