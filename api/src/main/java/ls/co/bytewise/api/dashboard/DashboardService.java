package ls.co.bytewise.api.dashboard;

import ls.co.bytewise.api.attendance.AttendanceRepository;
import ls.co.bytewise.api.common.Status;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.student.StudentStatus;
import ls.co.bytewise.api.studentfee.StudentFeeAccountRepository;
import ls.co.bytewise.api.teacher.TeacherRepository;
import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.term.TermRepository;
import ls.co.bytewise.api.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentFeeAccountRepository feeAccountRepository;
    private final TermRepository termRepository;

    public DashboardService(StudentRepository studentRepository,
                            UserRepository userRepository,
                            AttendanceRepository attendanceRepository,
                            StudentFeeAccountRepository feeAccountRepository,
                            TermRepository termRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.attendanceRepository = attendanceRepository;
        this.feeAccountRepository = feeAccountRepository;
        this.termRepository = termRepository;
    }

    public DashboardStatsDto getStats() {
        Long schoolId = TenantContext.getSchoolId();
        DashboardStatsDto stats = new DashboardStatsDto();

        stats.setTotalStudents(studentRepository.countBySchoolIdAndStatus(schoolId, StudentStatus.ACTIVE));
        stats.setTotalTeachers(userRepository.countActiveByRoleAndSchool(UserRole.TEACHER, schoolId));

        Term currentTerm = getCurrentTerm(schoolId);
        if (currentTerm != null) {
            stats.setAttendanceRateTerm(calculateAttendanceRate(schoolId, currentTerm));
            stats.setFeeCollectionRateTerm(calculateFeeCollectionRate(schoolId, currentTerm.getId()));
        }

        return stats;
    }

    private Term getCurrentTerm(Long schoolId) {
        LocalDate today = LocalDate.now();
        return termRepository.findBySchoolIdOrderByStartDateDesc(schoolId).stream()
                .filter(t -> {
                    LocalDate start = t.getStartDate().atZone(ZoneOffset.UTC).toLocalDate();
                    LocalDate end = t.getEndDate().atZone(ZoneOffset.UTC).toLocalDate();
                    return !today.isBefore(start) && !today.isAfter(end);
                })
                .findFirst()
                .orElse(null);
    }

    private double calculateAttendanceRate(Long schoolId, Term term) {
        LocalDate start = term.getStartDate().atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate end = term.getEndDate().atZone(ZoneOffset.UTC).toLocalDate();

        List<Object[]> counts = attendanceRepository.countAttendanceByStatusForSchool(schoolId, start, end);

        long total = 0;
        long present = 0;

        for (Object[] row : counts) {
            String status = row[0].toString();
            long count = (Long) row[1];
            total += count;
            if (status.equals("PRESENT")) {
                present += count;
            }
        }

        if (total == 0) return 0.0;
        return Math.round((present * 10000.0) / total) / 100.0;
    }

    private double calculateFeeCollectionRate(Long schoolId, Long termId) {
        List<Object[]> results = feeAccountRepository.sumFeesForTerm(schoolId, termId);

        if (results == null || results.isEmpty()) return 0.0;

        Object[] result = results.get(0);
        if (result == null || result.length < 2) return 0.0;

        double expected = ((Number) result[0]).doubleValue();
        double paid = ((Number) result[1]).doubleValue();

        if (expected <= 0) return 0.0;
        return Math.round((paid * 10000.0) / expected) / 100.0;
    }
}