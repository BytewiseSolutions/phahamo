package ls.co.bytewise.api.report;

import ls.co.bytewise.api.attendance.AttendanceRepository;
import ls.co.bytewise.api.reportcard.*;
import ls.co.bytewise.api.result.TermResultRepository;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.studentfee.StudentFeeAccountRepository;
import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.term.TermRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final TermResultRepository termResultRepository;
    private final StudentFeeAccountRepository feeAccountRepository;
    private final TermRepository termRepository;

    public ReportService(StudentRepository studentRepository,
                         AttendanceRepository attendanceRepository,
                         TermResultRepository termResultRepository,
                         StudentFeeAccountRepository feeAccountRepository,
                         TermRepository termRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.termResultRepository = termResultRepository;
        this.feeAccountRepository = feeAccountRepository;
        this.termRepository = termRepository;
    }

    @Transactional(readOnly = true)
    public List<StudentReportRowDto> getStudentReport() {
        Long schoolId = TenantContext.getSchoolId();
        List<Object[]> rows = studentRepository.countStudentsByClass(schoolId);
        List<StudentReportRowDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            StudentReportRowDto dto = new StudentReportRowDto();
            dto.setClassName((String) row[0]);
            dto.setTotalStudents(((Number) row[1]).longValue());
            result.add(dto);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<AttendanceReportRowDto> getAttendanceReport(Long termId) {
        Long schoolId = TenantContext.getSchoolId();
        Term term = termRepository.findByIdAndSchoolId(termId, schoolId).orElseThrow();
        LocalDate start = term.getStartDate().atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate end = term.getEndDate().atZone(ZoneOffset.UTC).toLocalDate();

        List<Object[]> rows = attendanceRepository.getAttendanceByClass(schoolId, start, end);
        List<AttendanceReportRowDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            AttendanceReportRowDto dto = new AttendanceReportRowDto();
            dto.setClassName((String) row[0]);
            long total = ((Number) row[1]).longValue();
            dto.setTotalRecords(total);

            if (total > 0) {
                long present = row[2] != null ? ((Number) row[2]).longValue() : 0;
                long absent = row[3] != null ? ((Number) row[3]).longValue() : 0;
                long late = row[4] != null ? ((Number) row[4]).longValue() : 0;
                dto.setPresentPercent(Math.round((present * 10000.0) / total) / 100.0);
                dto.setAbsentPercent(Math.round((absent * 10000.0) / total) / 100.0);
                dto.setLatePercent(Math.round((late * 10000.0) / total) / 100.0);
            } else {
                dto.setPresentPercent(0.0);
                dto.setAbsentPercent(0.0);
                dto.setLatePercent(0.0);
            }
            result.add(dto);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<AcademicReportRowDto> getAcademicReport(Long termId) {
        Long schoolId = TenantContext.getSchoolId();
        List<Object[]> rows = termResultRepository.getAcademicReport(schoolId, termId);
        List<AcademicReportRowDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            AcademicReportRowDto dto = new AcademicReportRowDto();
            dto.setSubjectName((String) row[0]);
            long count = ((Number) row[1]).longValue();
            dto.setTotalStudents(count);
            double avg = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            dto.setAverageMark(Math.round(avg * 100.0) / 100.0);
            long passes = row[3] != null ? ((Number) row[3]).longValue() : 0;
            dto.setPassRate(count > 0 ? Math.round((passes * 10000.0) / count) / 100.0 : 0.0);
            double highest = row[4] != null ? ((Number) row[4]).doubleValue() : 0.0;
            dto.setHighestMark(highest);
            result.add(dto);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<FinancialReportRowDto> getFinancialReport(Long termId) {
        Long schoolId = TenantContext.getSchoolId();
        List<Object[]> rows = feeAccountRepository.getFinancialReport(schoolId, termId);
        List<FinancialReportRowDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            FinancialReportRowDto dto = new FinancialReportRowDto();
            dto.setClassName((String) row[0]);
            dto.setExpectedFees(row[1] != null ? ((Number) row[1]).doubleValue() : 0.0);
            dto.setCollectedFees(row[2] != null ? ((Number) row[2]).doubleValue() : 0.0);
            dto.setOutstandingFees(row[3] != null ? ((Number) row[3]).doubleValue() : 0.0);
            result.add(dto);
        }
        return result;
    }
}