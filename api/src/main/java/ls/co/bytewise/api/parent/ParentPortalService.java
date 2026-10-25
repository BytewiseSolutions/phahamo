package ls.co.bytewise.api.parent;

import ls.co.bytewise.api.attendance.report.AttendanceReportService;
import ls.co.bytewise.api.attendance.report.StudentAttendanceReportDto;
import ls.co.bytewise.api.result.TermResultDto;
import ls.co.bytewise.api.result.TermResultService;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.studentfee.StudentFeeAccountDto;
import ls.co.bytewise.api.studentfee.StudentFeeAccountService;
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

    public ParentPortalService(ParentRepository parentRepository,
                               StudentRepository studentRepository,
                               TermRepository termRepository,
                               AttendanceReportService attendanceReportService,
                               TermResultService termResultService,
                               StudentFeeAccountService feeAccountService) {
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
        this.termRepository = termRepository;
        this.attendanceReportService = attendanceReportService;
        this.termResultService = termResultService;
        this.feeAccountService = feeAccountService;
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
