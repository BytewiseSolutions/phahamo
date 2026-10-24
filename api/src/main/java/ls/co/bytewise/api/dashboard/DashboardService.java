package ls.co.bytewise.api.dashboard;

import ls.co.bytewise.api.attendance.AttendanceRepository;
import ls.co.bytewise.api.attendance.AttendanceStatus;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.student.StudentStatus;
import ls.co.bytewise.api.studentfee.StudentFeeAccountRepository;
import ls.co.bytewise.api.subject.subjectclassassignment.SubjectClassAssignmentRepository;
import ls.co.bytewise.api.subscription.Subscription;
import ls.co.bytewise.api.subscription.SubscriptionRepository;
import ls.co.bytewise.api.subscription.SubscriptionService;
import ls.co.bytewise.api.subscription.SubscriptionStatus;
import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.term.TermRepository;
import ls.co.bytewise.api.timetable.Timetable;
import ls.co.bytewise.api.timetable.TimetableDto;
import ls.co.bytewise.api.timetable.TimetableRepository;
import ls.co.bytewise.api.timetable.lesson.LessonRepository;
import ls.co.bytewise.api.timetable.lesson.LessonStatus;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentFeeAccountRepository feeAccountRepository;
    private final TermRepository termRepository;
    private final TimetableRepository timetableRepository;
    private final SubjectClassAssignmentRepository assignmentRepository;
    private final LessonRepository lessonRepository;
    private final SchoolRepository schoolRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;


    public DashboardService(StudentRepository studentRepository,
                            UserRepository userRepository,
                            AttendanceRepository attendanceRepository,
                            StudentFeeAccountRepository feeAccountRepository,
                            TermRepository termRepository,
                            TimetableRepository timetableRepository,
                            SubjectClassAssignmentRepository assignmentRepository,
                            LessonRepository lessonRepository,
                            SchoolRepository schoolRepository,
                            SubscriptionRepository subscriptionRepository,
                            SubscriptionService subscriptionService
                            ) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.attendanceRepository = attendanceRepository;
        this.feeAccountRepository = feeAccountRepository;
        this.termRepository = termRepository;
        this.timetableRepository = timetableRepository;
        this.assignmentRepository = assignmentRepository;
        this.lessonRepository = lessonRepository;
        this.schoolRepository = schoolRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionService = subscriptionService;
    }

    public SystemDashboardStats getSystemStats() {
        SystemDashboardStats stats = new SystemDashboardStats();

        stats.setTotalSchools(schoolRepository.count());
        stats.setTotalUsers(userRepository.count());

        List<Subscription> subs = subscriptionRepository.findAll();
        Instant now = Instant.now();
        Instant sevenDaysFromNow = now.plus(7, ChronoUnit.DAYS);

        long active = 0, trial = 0, suspended = 0, expiring = 0;

        for (Subscription sub : subs) {
            SubscriptionStatus effective = subscriptionService.computeEffectiveStatus(sub);

            if (effective == SubscriptionStatus.SUSPENDED) suspended++;
            else if (effective == SubscriptionStatus.ACTIVE) active++;
            else if (effective == SubscriptionStatus.TRIAL) trial++;

            if (sub.getEndsAt() != null
                    && sub.getEndsAt().isAfter(now)
                    && sub.getEndsAt().isBefore(sevenDaysFromNow)) {
                expiring++;
            }
        }

        stats.setActiveSchools(active);
        stats.setTrialSchools(trial);
        stats.setSuspendedSchools(suspended);
        stats.setExpiringSoonSchools(expiring);
        return stats;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getAdminStats() {
        Long schoolId = TenantContext.getSchoolId();
        DashboardStatsDto stats = new DashboardStatsDto();

        stats.setTotalStudents(studentRepository.countBySchoolIdAndStatus(schoolId, StudentStatus.ACTIVE));
        stats.setTotalTeachers(userRepository.countActiveByRoleAndSchool(UserRole.TEACHER, schoolId));

        Term currentTerm = getCurrentTerm(schoolId);
        if (currentTerm != null) {
            stats.setAttendanceRateTerm(calculateAttendanceRate(schoolId, currentTerm, null));

            List<Object[]> feeResults = feeAccountRepository.sumFeesForTerm(schoolId, currentTerm.getId());
            if (feeResults != null && !feeResults.isEmpty() && feeResults.get(0) != null) {
                Object[] row = feeResults.get(0);
                double expected = ((Number) row[0]).doubleValue();
                double paid = ((Number) row[1]).doubleValue();
                if (expected > 0) {
                    stats.setFeeCollectionRateTerm(Math.round((paid * 10000.0) / expected) / 100.0);
                }
            }
        }

        return stats;
    }

    @Transactional(readOnly = true)
    public StudentDashboardStatsDto getStudentStats(User currentUser) {
        Long schoolId = TenantContext.getSchoolId();
        Student student = studentRepository.findByUserId(currentUser.getId())
                .filter(s -> s.getSchool().getId().equals(schoolId))
                .orElseThrow(() -> new IllegalArgumentException("No student record linked"));

        StudentDashboardStatsDto stats = new StudentDashboardStatsDto();

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        List<TimetableDto> todaySlots = timetableRepository
                .findAllByAssignment_SchoolClass_IdAndSchoolId(student.getCurrentClass().getId(), schoolId)
                .stream()
                .filter(t -> t.getDayOfWeek() == today)
                .map(TimetableDto::new)
                .toList();
        stats.setTodayTimetable(todaySlots);

        Term currentTerm = getCurrentTerm(schoolId);
        if (currentTerm != null) {
            stats.setAttendanceRateTerm(calculateAttendanceRate(schoolId, currentTerm, student.getId()));
            feeAccountRepository.findByStudentIdAndTermIdAndSchoolId(student.getId(), currentTerm.getId(), schoolId)
                    .ifPresent(account -> stats.setFeeBalance(account.getBalance()));
        }

        return stats;
    }

    @Transactional(readOnly = true)
    public ChartDataDto getAdminAttendanceChart() {
        Long schoolId = TenantContext.getSchoolId();
        Term term = getCurrentTerm(schoolId);
        return buildWeeklyChart(schoolId, term, null);
    }

    @Transactional(readOnly = true)
    public ChartDataDto getStudentAttendanceChart(User currentUser) {
        Long schoolId = TenantContext.getSchoolId();
        Student student = studentRepository.findByUserId(currentUser.getId()).orElseThrow();
        Term term = getCurrentTerm(schoolId);
        return buildWeeklyChart(schoolId, term, student.getId());
    }

    @Transactional(readOnly = true)
    public FeeChartDto getAdminFeeChart() {
        Long schoolId = TenantContext.getSchoolId();
        Term term = getCurrentTerm(schoolId);
        FeeChartDto dto = new FeeChartDto();
        dto.setExpected(0.0);
        dto.setPaid(0.0);

        if (term != null) {
            List<Object[]> results = feeAccountRepository.sumFeesForTerm(schoolId, term.getId());
            if (results != null && !results.isEmpty() && results.get(0) != null) {
                Object[] row = results.get(0);
                dto.setExpected(((Number) row[0]).doubleValue());
                dto.setPaid(((Number) row[1]).doubleValue());
            }
        }
        return dto;
    }

    private ChartDataDto buildWeeklyChart(Long schoolId, Term term, Long studentId) {
        ChartDataDto chart = new ChartDataDto();
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();

        if (term == null) {
            chart.setLabels(labels);
            chart.setData(data);
            return chart;
        }

        LocalDate termStart = term.getStartDate().atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate termEnd = term.getEndDate().atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate today = LocalDate.now();

        if (today.isBefore(termEnd)) {
            termEnd = today;
        }

        for (int i = 0; i < 4; i++) {
            LocalDate weekStart = termStart.plusWeeks(i);
            LocalDate weekEnd = weekStart.plusDays(6);

            if (weekStart.isAfter(termEnd)) break;
            if (weekEnd.isAfter(termEnd)) weekEnd = termEnd;

            List<Object[]> results = attendanceRepository.getAttendanceSummary(schoolId, weekStart, weekEnd, studentId, AttendanceStatus.PRESENT);
            labels.add("Week " + (i + 1));
            if (results != null && !results.isEmpty() && results.get(0) != null) {
                Object[] result = results.get(0);
                long total = result[0] != null ? ((Number) result[0]).longValue() : 0;
                long present = result[1] != null ? ((Number) result[1]).longValue() : 0;
                if (total == 0) {
                    data.add(0.0);
                } else {
                    data.add(Math.round((present * 10000.0) / total) / 100.0);
                }
            } else {
                data.add(0.0);
            }
        }

        chart.setLabels(labels);
        chart.setData(data);
        return chart;
    }

    private double calculateAttendanceRate(Long schoolId, Term term, Long studentId) {
        LocalDate start = term.getStartDate().atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate end = term.getEndDate().atZone(ZoneOffset.UTC).toLocalDate();
        List<Object[]> results = attendanceRepository.getAttendanceSummary(schoolId, start, end, studentId, AttendanceStatus.PRESENT);

        if (results == null || results.isEmpty() || results.get(0) == null) {
            return 0.0;
        }

        Object[] row = results.get(0);
        long total = row[0] != null ? ((Number) row[0]).longValue() : 0;
        long present = row[1] != null ? ((Number) row[1]).longValue() : 0;

        if (total > 0) {
            return Math.round((present * 10000.0) / total) / 100.0;
        }
        return 0.0;
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

    @Transactional(readOnly = true)
    public TeacherDashboardStatsDto getTeacherStats(User currentUser) {
        Long schoolId = TenantContext.getSchoolId();
        TeacherDashboardStatsDto stats = new TeacherDashboardStatsDto();

        long classes = assignmentRepository.countByTeacherIdAndSchoolId(currentUser.getId(), schoolId);
        stats.setTotalClasses(classes);

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        List<Timetable> todaysSlots = timetableRepository
                .findAllByAssignment_Teacher_IdAndSchoolId(currentUser.getId(), schoolId)
                .stream()
                .filter(t -> t.getDayOfWeek() == today)
                .toList();
        stats.setTodayPeriods(todaysSlots.size());

        LocalDate todayDate = LocalDate.now();
        long pending = 0;
        for (Timetable slot : todaysSlots) {
            boolean exists = lessonRepository.existsByTimetableIdAndDateAndStatus(
                    slot.getId(), todayDate, LessonStatus.COMPLETED);
            if (!exists) {
                pending++;
            }
        }
        stats.setPendingAttendance(pending);

        return stats;
    }
}