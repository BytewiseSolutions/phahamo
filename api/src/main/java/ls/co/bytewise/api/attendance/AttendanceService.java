package ls.co.bytewise.api.attendance;

import ls.co.bytewise.api.audit.AuditAction;
import ls.co.bytewise.api.audit.AuditLogService;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.holiday.HolidayService;
import ls.co.bytewise.api.mail.MailService;
import ls.co.bytewise.api.parent.Parent;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.timetable.lesson.Lesson;
import ls.co.bytewise.api.timetable.lesson.LessonRepository;
import ls.co.bytewise.api.timetable.lesson.LessonStatus;
import ls.co.bytewise.api.timetable.period.PeriodType;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final LessonRepository lessonRepository;
    private final AbsenceAlertLogRepository absenceAlertLogRepository;
    private final MailService mailService;
    private final HolidayService holidayService;
    private final AuditLogService auditLogService;


    public AttendanceService(
            AttendanceRepository attendanceRepository,
            StudentRepository studentRepository,
            SchoolClassRepository schoolClassRepository,
            LessonRepository lessonRepository,
            AbsenceAlertLogRepository absenceAlertLogRepository,
            MailService mailService,
            HolidayService holidayService,
            AuditLogService auditLogService
    ) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.lessonRepository = lessonRepository;
        this.absenceAlertLogRepository = absenceAlertLogRepository;
        this.mailService = mailService;
        this.holidayService = holidayService;
        this.auditLogService = auditLogService;
    }

    public List<AttendanceDto> getAttendanceForLesson(User requester, Long lessonId) {
        Long schoolId = TenantContext.getSchoolId();
        Lesson lesson = resolveLesson(lessonId, schoolId);
        ensureNotHoliday(schoolId, lesson);
        ensureCanMarkLesson(requester, lesson);

        SchoolClass schoolClass = lesson.getTimetable().getAssignment().getSchoolClass();
        List<Student> students = studentRepository.findAllBySchoolIdAndCurrentClassId(schoolId, schoolClass.getId());
        List<Attendance> existing = attendanceRepository.findAllByLessonIdAndSchoolId(lessonId, schoolId);

        return students.stream()
                .map(student -> existing.stream()
                        .filter(a -> a.getStudent().getId().equals(student.getId()))
                        .findFirst()
                        .map(AttendanceDto::new)
                        .orElseGet(() -> blankDto(student, schoolClass, lesson)))
                .toList();
    }

    @Transactional
    public List<AttendanceDto> markBulkAttendanceForLesson(
            User marker, Long lessonId, List<AttendanceDto> records
    ) {
        Long schoolId = TenantContext.getSchoolId();
        Lesson lesson = resolveLesson(lessonId, schoolId);
        ensureNotHoliday(schoolId, lesson);
        ensureCanMarkLesson(marker, lesson);
        SchoolClass schoolClass = lesson.getTimetable().getAssignment().getSchoolClass();

        List<AttendanceDto> result = records.stream()
                .map(r -> upsert(marker, schoolClass, lesson, r.getStudentId(), r.getStatus()))
                .map(AttendanceDto::new)
                .toList();

        lesson.setStatus(LessonStatus.COMPLETED);
        lesson.setUpdatedBy(marker);
        lessonRepository.save(lesson);
        auditLogService.record(lesson.getSchool(), marker, AuditAction.ATTENDANCE_CHANGED,
                "LESSON", lesson.getId(), "Updated attendance for " + schoolClass.getName()
                        + " on " + lesson.getDate() + " for " + records.size() + " student(s)");

        records.stream()
                .filter(r -> r.getStatus() == AttendanceStatus.ABSENT)
                .forEach(r -> {
                    Student student = studentRepository.findByIdAndSchoolId(r.getStudentId(), schoolId).orElse(null);
                    if (student != null) {
                        triggerAbsenceAlert(student, schoolClass, lesson.getDate());
                    }
                });

        return result;
    }

    @Transactional
    public AttendanceDto markSingleAttendanceForLesson(
            User marker, Long lessonId, Long studentId, AttendanceStatus status
    ) {
        Long schoolId = TenantContext.getSchoolId();
        Lesson lesson = resolveLesson(lessonId, schoolId);
        ensureNotHoliday(schoolId, lesson);
        ensureCanMarkLesson(marker, lesson);
        SchoolClass schoolClass = lesson.getTimetable().getAssignment().getSchoolClass();
        Attendance saved = upsert(marker, schoolClass, lesson, studentId, status);
        auditLogService.record(lesson.getSchool(), marker, AuditAction.ATTENDANCE_CHANGED,
                "ATTENDANCE", saved.getId(), "Marked " + saved.getStudent().getUser().getFullName()
                        + " as " + status + " for " + lesson.getDate());

        if (status == AttendanceStatus.ABSENT) {
            Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId).orElse(null);
            if (student != null) {
                triggerAbsenceAlert(student, schoolClass, lesson.getDate());
            }
        }

        return new AttendanceDto(saved);
    }

    private void triggerAbsenceAlert(Student student, SchoolClass schoolClass, LocalDate lessonDate) {
        School school = schoolClass.getSchool();

        if (!school.isAbsenceAlertsEnabled()) {
            return;
        }

        LocalDate absenceDate = lessonDate;  // no conversion needed

        if (absenceAlertLogRepository.existsByStudentIdAndAbsenceDate(student.getId(), absenceDate)) {
            return;
        }

        try {
            AbsenceAlertLog log = new AbsenceAlertLog();
            log.setStudent(student);
            log.setAbsenceDate(absenceDate);
            log.setSentAt(java.time.Instant.now());
            log.setSchool(school);
            absenceAlertLogRepository.save(log);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return;
        }

        String subject = "Absence Notification — " + student.getUser().getFullName();
        String body = buildAbsenceEmailBody(student, schoolClass, school, absenceDate);

        for (Parent parent : student.getParents()) {
            String email = parent.getUser() != null ? parent.getUser().getEmail() : null;
            if (email != null && !email.isBlank()) {
                mailService.send(email, subject, body);
            }
        }
    }

    private String buildAbsenceEmailBody(Student student, SchoolClass schoolClass, School school, LocalDate date) {
        String studentName = student.getUser().getFullName();
        String className = schoolClass.getName();
        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));

        return "Dear Parent,\n\n" +
                studentName + " in " + className + " was marked absent on " + formattedDate + ".\n\n" +
                "If this is incorrect, please contact the school.\n\n" +
                "Regards,\n" +
                school.getName();
    }

    private Attendance upsert(User marker, SchoolClass schoolClass, Lesson lesson, Long studentId, AttendanceStatus status) {
        Long schoolId = TenantContext.getSchoolId();

        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new AttendanceExceptions.StudentNotFoundException("Student not found"));

        Attendance attendance = attendanceRepository
                .findByStudentIdAndLessonIdAndSchoolId(studentId, lesson.getId(), schoolId)
                .orElseGet(Attendance::new);

        boolean isNew = attendance.getId() == null;

        attendance.setStudent(student);
        attendance.setSchoolClass(schoolClass);
        attendance.setLesson(lesson);
        attendance.setDate(lesson.getDate());
        attendance.setStatus(status);
        attendance.setMarkedBy(marker);
        attendance.setSchool(schoolClass.getSchool());
        attendance.setUpdatedBy(marker);
        if (isNew) {
            attendance.setCreatedBy(marker);
        }

        return attendanceRepository.save(attendance);
    }

    private Lesson resolveLesson(Long lessonId, Long schoolId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AttendanceExceptions.LessonNotFoundException("Lesson not found"));
        if (!lesson.getSchool().getId().equals(schoolId)) {
            throw new AttendanceExceptions.LessonNotFoundException("Lesson not found");
        }
        return lesson;
    }

    private void ensureCanMarkLesson(User requester, Lesson lesson) {
        boolean isOverrideRole = requester.hasRole(UserRole.SCHOOL_ADMIN)
                || requester.hasRole(UserRole.SYSTEM_ADMIN)
                || requester.hasRole(UserRole.PRINCIPAL);

        if (isOverrideRole) return;

        var assignment = lesson.getTimetable().getAssignment();
        boolean isSubjectTeacher = assignment.getTeacher() != null
                && assignment.getTeacher().getId().equals(requester.getId());

        SchoolClass schoolClass = assignment.getSchoolClass();
        boolean isClassTeacher = schoolClass.getClassTeacher() != null
                && schoolClass.getClassTeacher().getId().equals(requester.getId());

        if (!isSubjectTeacher && !isClassTeacher) {
            throw new AttendanceExceptions.NotClassTeacherException("You are not authorised to mark attendance for this lesson");
        }
    }

    private void ensureNotHoliday(Long schoolId, Lesson lesson) {
        if (lesson.getTimetable().getPeriod().getType() != null
                && lesson.getTimetable().getPeriod().getType() != PeriodType.LESSON) {
            throw new AttendanceExceptions.InvalidAttendanceException(
                    "Attendance cannot be recorded for a break or lunch period");
        }
        if (holidayService.isHoliday(schoolId, lesson.getDate())) {
            throw new AttendanceExceptions.InvalidAttendanceException(
                    "Attendance cannot be recorded on a school holiday");
        }
        if (lesson.getStatus() == LessonStatus.CANCELLED) {
            throw new AttendanceExceptions.InvalidAttendanceException(
                    "Attendance cannot be recorded for a cancelled lesson");
        }
    }

    private AttendanceDto blankDto(Student student, SchoolClass schoolClass, Lesson lesson) {
        AttendanceDto dto = new AttendanceDto();
        dto.setStudentId(student.getId());
        dto.setStudentFirstName(student.getUser().getFirstName());
        dto.setStudentLastName(student.getUser().getLastName());
        dto.setSchoolClassId(schoolClass.getId());
        dto.setSchoolClassName(schoolClass.getName());
        dto.setLessonId(lesson.getId());
        dto.setDate(lesson.getDate());
        dto.setStatus(null);
        return dto;
    }
}
