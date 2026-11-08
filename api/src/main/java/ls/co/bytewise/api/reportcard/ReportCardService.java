package ls.co.bytewise.api.reportcard;

import ls.co.bytewise.api.attendance.Attendance;
import ls.co.bytewise.api.attendance.AttendanceRepository;
import ls.co.bytewise.api.attendance.AttendanceStatus;
import ls.co.bytewise.api.grading.GradeBandRepository;
import ls.co.bytewise.api.result.TermResult;
import ls.co.bytewise.api.result.TermResultRepository;
import ls.co.bytewise.api.result.TermResultStatus;
import ls.co.bytewise.api.result.remark.Remark;
import ls.co.bytewise.api.result.remark.RemarkService;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.schoolclass.SchoolClassRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.term.TermRepository;
import ls.co.bytewise.api.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportCardService {

    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final TermRepository termRepository;
    private final TermResultRepository termResultRepository;
    private final AttendanceRepository attendanceRepository;
    private final GradeBandRepository gradeBandRepository;
    private final RemarkService remarkService;

    public ReportCardService(StudentRepository studentRepository,
                             SchoolClassRepository schoolClassRepository,
                             TermRepository termRepository,
                             TermResultRepository termResultRepository,
                             AttendanceRepository attendanceRepository,
                             GradeBandRepository gradeBandRepository,
                             RemarkService remarkService) {
        this.studentRepository = studentRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.termRepository = termRepository;
        this.termResultRepository = termResultRepository;
        this.attendanceRepository = attendanceRepository;
        this.gradeBandRepository = gradeBandRepository;
        this.remarkService = remarkService;
    }

    public ReportCardDto buildForStudent(Long studentId, Long termId, User generatedBy) {
        Long schoolId = TenantContext.getSchoolId();

        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new ReportCardExceptions.ReportCardNotFoundException("Student not found"));

        Term term = termRepository.findByIdAndSchoolId(termId, schoolId)
                .orElseThrow(() -> new ReportCardExceptions.ReportCardNotFoundException("Term not found"));

        SchoolClass schoolClass = student.getCurrentClass();
        if (schoolClass == null) {
            throw new ReportCardExceptions.ReportCardNotFoundException(
                    "Student has no current class assigned");
        }

        School school = schoolClass.getSchool();

        List<TermResult> results = termResultRepository
                .findAllByStudentIdAndTermIdAndSchoolId(studentId, termId, schoolId)
                .stream()
                .filter(r -> r.getStatus() == TermResultStatus.FINALISED)
                .toList();

        ReportCardDto dto = new ReportCardDto();
        dto.setSchoolName(school.getName());
        dto.setSchoolAddress(school.getAddress());
        dto.setSchoolPhone(school.getContactPhone());
        dto.setSchoolEmail(school.getContactEmail());
        if (school.getLogo() != null) {
            dto.setSchoolLogo(school.getLogo().getContents());
            dto.setSchoolLogoMimeType(school.getLogo().getType());
        }

        dto.setStudentFullName(student.getUser().getFullName());
        String firstName = student.getUser().getFirstName();
        String lastName = student.getUser().getLastName();
        dto.setStudentInitials((firstName == null ? "" : firstName.substring(0, Math.min(1, firstName.length())))
                + (lastName == null ? "" : lastName.substring(0, Math.min(1, lastName.length()))));
        if (student.getPhoto() != null) {
            dto.setStudentPhoto(student.getPhoto().getContents());
            dto.setStudentPhotoMimeType(student.getPhoto().getType());
        }
        dto.setAdmissionNumber(student.getAdmissionNumber());
        dto.setClassName(schoolClass.getName());
        dto.setTermName(term.getName());
        dto.setAcademicYearName(term.getAcademicYear() != null
                ? term.getAcademicYear().getName() : null);

        dto.setSubjects(results.stream().map(r -> {
            ReportCardDto.SubjectLineDto line = new ReportCardDto.SubjectLineDto();
            line.setSubjectName(r.getSubject().getName());
            line.setFinalMark(r.getFinalMark());
            line.setGrade(r.getGrade());
            line.setRemark(r.getRemark());
            return line;
        }).toList());

        if (!results.isEmpty()) {
            double avg = results.stream()
                    .mapToDouble(TermResult::getFinalMark)
                    .average()
                    .orElse(0.0);
            dto.setAverageMark(Math.round(avg * 100.0) / 100.0);
            dto.setOverallGrade(overallGradeFor(dto.getAverageMark(), schoolId));
        }

        Remark remark = remarkService.getOrCreate(student, termId, schoolId);
        dto.setClassTeacherRemark(remark.getClassTeacherRemark());
        dto.setPrincipalRemark(remark.getPrincipalRemark());

        attachAttendance(dto, studentId, schoolId, term);

        if (schoolClass.getClassTeacher() != null) {
            dto.setPrincipalName(schoolClass.getClassTeacher().getFullName());
        } else {
            dto.setPrincipalName("");
        }
        dto.setGeneratedDate(LocalDate.now());

        return dto;
    }

    private String overallGradeFor(Double average, Long schoolId) {
        if (average == null) return null;
        return gradeBandRepository.findAllBySchoolIdOrderByMinScoreDesc(schoolId).stream()
                .filter(b -> average >= b.getMinScore() && average <= b.getMaxScore())
                .map(b -> b.getGrade())
                .findFirst()
                .orElse(null);
    }

    private void attachAttendance(ReportCardDto dto, Long studentId, Long schoolId, Term term) {
        LocalDate from = term.getStartDate() == null ? null
                : term.getStartDate().atZone(java.time.ZoneOffset.UTC).toLocalDate();
        LocalDate to = term.getEndDate() == null ? null
                : term.getEndDate().atZone(java.time.ZoneOffset.UTC).toLocalDate();

        List<Attendance> rows = attendanceRepository.findForReport(
                schoolId, from, to, null, null, studentId);

        int present = (int) rows.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        int absent  = (int) rows.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        int late    = (int) rows.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
        int excused = (int) rows.stream().filter(a -> a.getStatus() == AttendanceStatus.EXCUSED).count();
        int total   = rows.size();

        dto.setAttendancePresent(present);
        dto.setAttendanceAbsent(absent);
        dto.setAttendanceLate(late);
        dto.setAttendanceExcused(excused);
        dto.setAttendanceTotal(total);
        dto.setAttendancePercent(total == 0 ? 0.0
                : Math.round((present * 10000.0) / total) / 100.0);
    }

    public byte[] renderPdf(Long studentId, Long termId, User generatedBy) {
        ReportCardDto dto = buildForStudent(studentId, termId, generatedBy);
        return ReportCardRenderer.render(dto);
    }

    public List<Student> listStudentsForClass(Long classId) {
        Long schoolId = TenantContext.getSchoolId();
        return studentRepository.findAllBySchoolIdAndCurrentClassId(schoolId, classId);
    }

    public SchoolClass requireClass(Long classId) {
        Long schoolId = TenantContext.getSchoolId();
        return schoolClassRepository.findById(classId)
                .filter(c -> c.getSchool().getId().equals(schoolId))
                .orElseThrow(() -> new ReportCardExceptions.ReportCardNotFoundException("Class not found"));
    }

    public byte[] renderPdfForUser(User user, Long termId) {
        Long schoolId = TenantContext.getSchoolId();
        Student student = studentRepository.findByUserId(user.getId())
                .filter(s -> s.getSchool().getId().equals(schoolId))
                .orElseThrow(() -> new ReportCardExceptions.ReportCardNotFoundException(
                        "No student record linked to this account"));
        return renderPdf(student.getId(), termId, user);
    }
}
