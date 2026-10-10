package ls.co.bytewise.api.attendance;

import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.timetable.lesson.Lesson;
import ls.co.bytewise.api.timetable.lesson.LessonRepository;
import ls.co.bytewise.api.timetable.lesson.LessonStatus;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final LessonRepository lessonRepository;

    public AttendanceService(
            AttendanceRepository attendanceRepository,
            StudentRepository studentRepository,
            SchoolClassRepository schoolClassRepository,
            LessonRepository lessonRepository
    ) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.lessonRepository = lessonRepository;
    }

    public List<AttendanceDto> getAttendanceForLesson(User requester, Long lessonId) {
        Long schoolId = TenantContext.getSchoolId();
        Lesson lesson = resolveLesson(lessonId, schoolId);
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
        ensureCanMarkLesson(marker, lesson);

        SchoolClass schoolClass = lesson.getTimetable().getAssignment().getSchoolClass();

        List<AttendanceDto> result = records.stream()
                .map(r -> upsert(marker, schoolClass, lesson, r.getStudentId(), r.getStatus()))
                .map(AttendanceDto::new)
                .toList();

        lesson.setStatus(LessonStatus.COMPLETED);
        lesson.setUpdatedBy(marker);
        lessonRepository.save(lesson);

        return result;
    }

    @Transactional
    public AttendanceDto markSingleAttendanceForLesson(
            User marker, Long lessonId, Long studentId, AttendanceStatus status
    ) {
        Long schoolId = TenantContext.getSchoolId();
        Lesson lesson = resolveLesson(lessonId, schoolId);
        ensureCanMarkLesson(marker, lesson);

        SchoolClass schoolClass = lesson.getTimetable().getAssignment().getSchoolClass();
        return new AttendanceDto(upsert(marker, schoolClass, lesson, studentId, status));
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