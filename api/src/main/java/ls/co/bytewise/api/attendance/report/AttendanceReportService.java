package ls.co.bytewise.api.attendance.report;

import ls.co.bytewise.api.attendance.Attendance;
import ls.co.bytewise.api.attendance.AttendanceDto;
import ls.co.bytewise.api.attendance.AttendanceRepository;
import ls.co.bytewise.api.attendance.AttendanceStatus;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.term.TermRepository;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.term.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AttendanceReportService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final TermRepository termRepository;

    public AttendanceReportService(AttendanceRepository attendanceRepository,
                                   StudentRepository studentRepository,
                                   TermRepository termRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.termRepository = termRepository;
    }

    public List<LessonAttendanceSummaryDto> lessonBreakdown(Long classId,
                                                            Long subjectId,
                                                            LocalDate fromDate,
                                                            LocalDate toDate) {
        Long schoolId = TenantContext.getSchoolId();

        List<Attendance> rows = attendanceRepository.findForReport(
                schoolId, fromDate, toDate, classId, subjectId, null);

        Map<Long, List<Attendance>> byLesson = rows.stream()
                .collect(Collectors.groupingBy(a -> a.getLesson().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<LessonAttendanceSummaryDto> result = new ArrayList<>();
        for (Map.Entry<Long, List<Attendance>> entry : byLesson.entrySet()) {
            List<Attendance> lessonRows = entry.getValue();
            Attendance sample = lessonRows.get(0);

            LessonAttendanceSummaryDto dto = new LessonAttendanceSummaryDto();
            dto.setLessonId(sample.getLesson().getId());
            dto.setDate(sample.getDate());
            dto.setPeriodName(sample.getLesson().getTimetable().getPeriod().getName());
            dto.setSubjectName(sample.getLesson().getTimetable().getAssignment().getSubject().getName());
            dto.setClassName(sample.getSchoolClass().getName());

            int total = lessonRows.size();
            int present = (int) lessonRows.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
            int absent = (int) lessonRows.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
            int late = (int) lessonRows.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
            int excused = (int) lessonRows.stream().filter(a -> a.getStatus() == AttendanceStatus.EXCUSED).count();

            dto.setTotal(total);
            dto.setPresent(present);
            dto.setAbsent(absent);
            dto.setLate(late);
            dto.setExcused(excused);

            dto.setPresentPercent(pct(present, total));
            dto.setAbsentPercent(pct(absent, total));
            dto.setLatePercent(pct(late, total));
            dto.setExcusedPercent(pct(excused, total));

            result.add(dto);
        }

        result.sort(Comparator.comparing(LessonAttendanceSummaryDto::getDate).reversed()
                .thenComparing(LessonAttendanceSummaryDto::getLessonId).reversed());

        return result;
    }

    public List<StudentAttendanceSummaryDto> classBreakdown(Long classId,
                                                            Long subjectId,
                                                            LocalDate fromDate,
                                                            LocalDate toDate) {
        Long schoolId = TenantContext.getSchoolId();

        List<Attendance> rows = attendanceRepository.findForReport(
                schoolId, fromDate, toDate, classId, subjectId, null);

        Map<Long, List<Attendance>> byStudent = rows.stream()
                .collect(Collectors.groupingBy(a -> a.getStudent().getId()));

        List<Student> students = classId == null
                ? studentRepository.findAllBySchoolId(schoolId)
                : studentRepository.findAllBySchoolIdAndCurrentClassId(schoolId, classId);

        List<StudentAttendanceSummaryDto> result = new ArrayList<>();
        for (Student student : students) {
            List<Attendance> studentRows = byStudent.getOrDefault(student.getId(), List.of());

            StudentAttendanceSummaryDto dto = new StudentAttendanceSummaryDto();
            dto.setStudentId(student.getId());
            dto.setFirstName(student.getUser().getFirstName());
            dto.setLastName(student.getUser().getLastName());
            dto.setAdmissionNumber(student.getAdmissionNumber());

            int total = studentRows.size();
            int present = (int) studentRows.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
            int absent = (int) studentRows.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
            int late = (int) studentRows.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
            int excused = (int) studentRows.stream().filter(a -> a.getStatus() == AttendanceStatus.EXCUSED).count();

            dto.setTotal(total);
            dto.setPresent(present);
            dto.setAbsent(absent);
            dto.setLate(late);
            dto.setExcused(excused);

            dto.setPresentPercent(pct(present, total));
            dto.setAbsentPercent(pct(absent, total));
            dto.setLatePercent(pct(late, total));
            dto.setExcusedPercent(pct(excused, total));

            result.add(dto);
        }

        result.sort(Comparator
                .comparingDouble(StudentAttendanceSummaryDto::getPresentPercent)
                .thenComparing(StudentAttendanceSummaryDto::getLastName));

        return result;
    }

    public StudentAttendanceReportDto studentReport(Long studentId,
                                                    Long classId,
                                                    Long subjectId,
                                                    LocalDate fromDate,
                                                    LocalDate toDate) {
        Long schoolId = TenantContext.getSchoolId();

        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        List<Attendance> rows = attendanceRepository.findForReport(
                schoolId, fromDate, toDate, classId, subjectId, studentId);

        StudentAttendanceSummaryDto summary = new StudentAttendanceSummaryDto();
        summary.setStudentId(student.getId());
        summary.setFirstName(student.getUser().getFirstName());
        summary.setLastName(student.getUser().getLastName());
        summary.setAdmissionNumber(student.getAdmissionNumber());

        int total = rows.size();
        int present = (int) rows.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        int absent = (int) rows.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        int late = (int) rows.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
        int excused = (int) rows.stream().filter(a -> a.getStatus() == AttendanceStatus.EXCUSED).count();

        summary.setTotal(total);
        summary.setPresent(present);
        summary.setAbsent(absent);
        summary.setLate(late);
        summary.setExcused(excused);
        summary.setPresentPercent(pct(present, total));
        summary.setAbsentPercent(pct(absent, total));
        summary.setLatePercent(pct(late, total));
        summary.setExcusedPercent(pct(excused, total));

        StudentAttendanceReportDto report = new StudentAttendanceReportDto();
        report.setSummary(summary);
        report.setRecords(rows.stream().map(AttendanceDto::new).toList());
        return report;
    }

    private double pct(int n, int total) {
        if (total == 0) return 0.0;
        return Math.round((n * 10000.0) / total) / 100.0;
    }

    public StudentAttendanceReportDto studentReportForUser(User user,
                                                           Long termId,
                                                           LocalDate fromDate,
                                                           LocalDate toDate) {
        Long schoolId = TenantContext.getSchoolId();

        Student student = studentRepository.findByUserId(user.getId())
                .filter(s -> s.getSchool().getId().equals(schoolId))
                .orElseThrow(() -> new IllegalArgumentException("No student record linked to this account"));

        LocalDate effectiveFrom;
        LocalDate effectiveTo;

        if (termId != null) {
            Term term = termRepository.findByIdAndSchoolId(termId, schoolId)
                    .orElseThrow(() -> new IllegalArgumentException("Term not found"));
            effectiveFrom = term.getStartDate().atZone(ZoneId.systemDefault()).toLocalDate();
            effectiveTo   = term.getEndDate().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (fromDate != null && toDate != null) {
            effectiveFrom = fromDate;
            effectiveTo = toDate;
        } else {
            throw new IllegalArgumentException("Provide either termId or both from and to");
        }

        return studentReport(student.getId(), null, null, effectiveFrom, effectiveTo);
    }
}
