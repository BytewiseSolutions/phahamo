package ls.co.bytewise.api.attendance;

import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;

    public AttendanceService(
            AttendanceRepository attendanceRepository,
            StudentRepository studentRepository,
            SchoolClassRepository schoolClassRepository
    ) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.schoolClassRepository = schoolClassRepository;
    }

    public List<AttendanceDto> getAttendanceForClassAndDate(User requester, Long schoolClassId, LocalDate date) {
        Long schoolId = TenantContext.getSchoolId();
        SchoolClass schoolClass = resolveClass(schoolClassId);
        ensureCanAccessClass(requester, schoolClass);

        List<Student> students = studentRepository.findAllBySchoolIdAndCurrentClassId(schoolId, schoolClassId);
        List<Attendance> existing = attendanceRepository
                .findAllBySchoolClassIdAndDateAndSchoolId(schoolClassId, date, schoolId);

        return students.stream()
                .map(student -> existing.stream()
                        .filter(a -> a.getStudent().getId().equals(student.getId()))
                        .findFirst()
                        .map(AttendanceDto::new)
                        .orElseGet(() -> blankDto(student, schoolClass, date)))
                .toList();
    }

    @Transactional
    public List<AttendanceDto> markBulkAttendance(
            User marker, Long schoolClassId, LocalDate date, List<AttendanceDto> records
    ) {
        SchoolClass schoolClass = resolveClass(schoolClassId);
        ensureCanAccessClass(marker, schoolClass);

        return records.stream()
                .map(r -> upsert(marker, schoolClass, r.getStudentId(), date, r.getStatus()))
                .map(AttendanceDto::new)
                .toList();
    }

    @Transactional
    public AttendanceDto markSingleAttendance(
            User marker, Long schoolClassId, Long studentId, LocalDate date, AttendanceStatus status
    ) {
        SchoolClass schoolClass = resolveClass(schoolClassId);
        ensureCanAccessClass(marker, schoolClass);

        return new AttendanceDto(upsert(marker, schoolClass, studentId, date, status));
    }

    private Attendance upsert(User marker, SchoolClass schoolClass, Long studentId, LocalDate date, AttendanceStatus status) {
        Long schoolId = TenantContext.getSchoolId();

        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new AttendanceExceptions.StudentNotFoundException("Student not found"));

        Attendance attendance = attendanceRepository
                .findByStudentIdAndSchoolClassIdAndDateAndSchoolId(studentId, schoolClass.getId(), date, schoolId)
                .orElseGet(Attendance::new);

        boolean isNew = attendance.getId() == null;

        attendance.setStudent(student);
        attendance.setSchoolClass(schoolClass);
        attendance.setDate(date);
        attendance.setStatus(status);
        attendance.setMarkedBy(marker);
        attendance.setSchool(schoolClass.getSchool());
        attendance.setUpdatedBy(marker);
        if (isNew) {
            attendance.setCreatedBy(marker);
        }

        return attendanceRepository.save(attendance);
    }

    private SchoolClass resolveClass(Long schoolClassId) {
        return schoolClassRepository.findById(schoolClassId)
                .orElseThrow(() -> new AttendanceExceptions.SchoolClassNotFoundException("Class not found"));
    }

    private void ensureCanAccessClass(User requester, SchoolClass schoolClass) {
        boolean isOverrideRole = requester.hasRole(UserRole.SCHOOL_ADMIN)
                || requester.hasRole(UserRole.SYSTEM_ADMIN)
                || requester.hasRole(UserRole.PRINCIPAL);

        boolean isClassTeacher = schoolClass.getClassTeacher() != null
                && schoolClass.getClassTeacher().getId().equals(requester.getId());

        if (!isOverrideRole && !isClassTeacher) {
            throw new AttendanceExceptions.NotClassTeacherException(
                    "You are not the class teacher for this class");
        }
    }

    private AttendanceDto blankDto(Student student, SchoolClass schoolClass, LocalDate date) {
        AttendanceDto dto = new AttendanceDto();
        dto.setStudentId(student.getId());
        dto.setStudentFirstName(student.getFirstName());
        dto.setStudentLastName(student.getLastName());
        dto.setSchoolClassId(schoolClass.getId());
        dto.setSchoolClassName(schoolClass.getName());
        dto.setDate(date);
        dto.setStatus(null);
        return dto;
    }
}