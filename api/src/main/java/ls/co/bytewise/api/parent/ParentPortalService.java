package ls.co.bytewise.api.parent;

import ls.co.bytewise.api.announcement.AnnouncementDto;
import ls.co.bytewise.api.announcement.AnnouncementService;
import ls.co.bytewise.api.attendance.AttendanceStatus;
import ls.co.bytewise.api.attendance.report.AttendanceReportService;
import ls.co.bytewise.api.attendance.report.StudentAttendanceReportDto;
import ls.co.bytewise.api.result.TermResultDto;
import ls.co.bytewise.api.result.TermResultService;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.studentfee.StudentFeeAccountDto;
import ls.co.bytewise.api.studentfee.StudentFeeAccountService;
import ls.co.bytewise.api.studentfee.StudentFeeExceptions;
import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.term.TermRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class ParentPortalService {
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final TermRepository termRepository;
    private final AttendanceReportService attendanceReportService;
    private final TermResultService termResultService;
    private final StudentFeeAccountService feeAccountService;
    private final AnnouncementService announcementService;

    public ParentPortalService(ParentRepository parentRepository,
                               StudentRepository studentRepository,
                               TermRepository termRepository,
                               AttendanceReportService attendanceReportService,
                               TermResultService termResultService,
                               StudentFeeAccountService feeAccountService,
                               AnnouncementService announcementService) {
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
        this.termRepository = termRepository;
        this.attendanceReportService = attendanceReportService;
        this.termResultService = termResultService;
        this.feeAccountService = feeAccountService;
        this.announcementService = announcementService;
    }

    @Transactional(readOnly = true)
    public ParentDashboardDto dashboard(User user) {
        Parent parent = currentParent(user);
        Long schoolId = TenantContext.getSchoolId();
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        Term currentTerm = termRepository.findBySchoolIdOrderByStartDateDesc(schoolId).stream()
                .filter(term -> !today.isBefore(term.getStartDate().atZone(ZoneId.systemDefault()).toLocalDate())
                        && !today.isAfter(term.getEndDate().atZone(ZoneId.systemDefault()).toLocalDate()))
                .findFirst()
                .orElse(null);

        List<ParentDashboardChildDto> summaries = studentRepository.findByParentsId(parent.getId()).stream()
                .filter(student -> student.getSchool().getId().equals(schoolId))
                .map(student -> dashboardChild(student, today, currentTerm))
                .toList();

        List<AnnouncementDto> announcements = announcementService.getFeedForUser(user);
        return new ParentDashboardDto(summaries, announcements);
    }

    private ParentDashboardChildDto dashboardChild(Student student, LocalDate today, Term currentTerm) {
        ParentChildDto child = new ParentChildDto(student);
        StudentAttendanceReportDto attendance = attendanceReportService.studentReport(
                student.getId(), null, null, today, today);
        String attendanceStatus = attendanceStatus(attendance);

        Double feeBalance = null;
        if (currentTerm != null) {
            try {
                feeBalance = feeAccountService.getAccount(student.getId(), currentTerm.getId()).getBalance();
            } catch (StudentFeeExceptions.AccountNotFoundException ignored) {
                // A student without a generated fee account has no balance to display yet.
            }
        }

        return new ParentDashboardChildDto(child, attendanceStatus, feeBalance);
    }

    private String attendanceStatus(StudentAttendanceReportDto attendance) {
        if (attendance.getRecords().isEmpty()) return "NOT_MARKED";
        if (attendance.getRecords().stream().anyMatch(record -> record.getStatus() == AttendanceStatus.ABSENT)) {
            return "ABSENT";
        }
        if (attendance.getRecords().stream().anyMatch(record -> record.getStatus() == AttendanceStatus.LATE)) {
            return "LATE";
        }
        if (attendance.getRecords().stream().allMatch(record -> record.getStatus() == AttendanceStatus.EXCUSED)) {
            return "EXCUSED";
        }
        return "PRESENT";
    }

    @Transactional(readOnly = true)
    public List<ParentChildDto> children(User user) {
        Parent parent = currentParent(user);
        return studentRepository.findByParentsId(parent.getId()).stream()
                .filter(student -> student.getSchool().getId().equals(TenantContext.getSchoolId()))
                .map(ParentChildDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentAttendanceReportDto attendance(User user, Long studentId, Long termId) {
        Student student = child(user, studentId);
        Term term = term(termId);
        LocalDate from = term.getStartDate().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate to = term.getEndDate().atZone(ZoneId.systemDefault()).toLocalDate();
        return attendanceReportService.studentReport(student.getId(), null, null, from, to);
    }

    @Transactional(readOnly = true)
    public List<TermResultDto> results(User user, Long studentId, Long termId) {
        Student student = child(user, studentId);
        term(termId);
        return termResultService.listForStudentAndTerm(student.getId(), termId);
    }

    @Transactional(readOnly = true)
    public StudentFeeAccountDto fees(User user, Long studentId, Long termId) {
        Student student = child(user, studentId);
        term(termId);
        return feeAccountService.getAccount(student.getId(), termId);
    }

    private Parent currentParent(User user) {
        Long schoolId = TenantContext.getSchoolId();
        return parentRepository.findByUserId(user.getId())
                .filter(parent -> parent.getSchool().getId().equals(schoolId))
                .filter(Parent::isLoginEnabled)
                .orElseThrow(() -> new IllegalArgumentException("Parent account is not linked to this school"));
    }

    private Student child(User user, Long studentId) {
        Parent parent = currentParent(user);
        if (!studentRepository.existsParentLink(studentId, parent.getId())) {
            throw new IllegalArgumentException("Student is not linked to this parent account");
        }
        return studentRepository.findByIdAndSchoolId(studentId, TenantContext.getSchoolId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
    }

    private Term term(Long termId) {
        return termRepository.findByIdAndSchoolId(termId, TenantContext.getSchoolId())
                .orElseThrow(() -> new IllegalArgumentException("Term not found"));
    }
}
