package ls.co.bytewise.api.dashboard;

import ls.co.bytewise.api.attendance.AttendanceRepository;
import ls.co.bytewise.api.attendance.AttendanceStatus;
import ls.co.bytewise.api.assessment.Assessment;
import ls.co.bytewise.api.assessment.AssessmentRepository;
import ls.co.bytewise.api.assessment.mark.MarkRepository;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.student.StudentRepository;
import ls.co.bytewise.api.student.StudentStatus;
import ls.co.bytewise.api.studentfee.StudentFeeAccountRepository;
import ls.co.bytewise.api.studentfee.StudentFeePaymentDto;
import ls.co.bytewise.api.studentfee.StudentFeePaymentRepository;
import ls.co.bytewise.api.examination.Examination;
import ls.co.bytewise.api.examination.ExaminationRepository;
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
import ls.co.bytewise.api.timetable.lesson.LessonStatus;
import ls.co.bytewise.api.timetable.lesson.LessonDto;
import ls.co.bytewise.api.timetable.lesson.LessonRepository;
import ls.co.bytewise.api.timetable.lesson.LessonService;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentFeeAccountRepository feeAccountRepository;
    private final TermRepository termRepository;
    private final TimetableRepository timetableRepository;
    private final SubjectClassAssignmentRepository assignmentRepository;
    private final SchoolRepository schoolRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;
    private final StudentFeePaymentRepository paymentRepository;
    private final ExaminationRepository examinationRepository;
    private final LessonService lessonService;
    private final AssessmentRepository assessmentRepository;
    private final MarkRepository markRepository;
    private final LessonRepository lessonRepository;


    public DashboardService(StudentRepository studentRepository,
                            UserRepository userRepository,
                            AttendanceRepository attendanceRepository,
                            StudentFeeAccountRepository feeAccountRepository,
                            TermRepository termRepository,
                            TimetableRepository timetableRepository,
                            SubjectClassAssignmentRepository assignmentRepository,
                            SchoolRepository schoolRepository,
                            SubscriptionRepository subscriptionRepository,
                            SubscriptionService subscriptionService,
                            StudentFeePaymentRepository paymentRepository,
                            ExaminationRepository examinationRepository,
                            LessonService lessonService,
                            AssessmentRepository assessmentRepository,
                            MarkRepository markRepository,
                            LessonRepository lessonRepository
                            ) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.attendanceRepository = attendanceRepository;
        this.feeAccountRepository = feeAccountRepository;
        this.termRepository = termRepository;
        this.timetableRepository = timetableRepository;
        this.assignmentRepository = assignmentRepository;
        this.schoolRepository = schoolRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionService = subscriptionService;
        this.paymentRepository = paymentRepository;
        this.examinationRepository = examinationRepository;
        this.lessonService = lessonService;
        this.assessmentRepository = assessmentRepository;
        this.markRepository = markRepository;
        this.lessonRepository = lessonRepository;
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

        LocalDate today = LocalDate.now();
        stats.setAttendanceRateToday(calculateAttendanceRate(schoolId, today));
        stats.setLessonsCancelledToday(lessonRepository.countBySchoolIdAndDateAndStatus(
                schoolId, today, LessonStatus.CANCELLED));
        stats.setLessonsSubstitutedToday(lessonRepository.countBySchoolIdAndDateAndStatus(
                schoolId, today, LessonStatus.SUBSTITUTED));
        stats.setUpcomingExamsCount(examinationRepository.findUpcomingBySchool(
                schoolId, today, today.plusDays(7)).size());

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
    public List<StudentFeePaymentDto> getRecentPayments() {
        Long schoolId = TenantContext.getSchoolId();
        return paymentRepository.findAllBySchoolIdOrderByPaidAtDescIdDesc(schoolId, PageRequest.of(0, 5))
                .stream()
                .map(StudentFeePaymentDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UpcomingExamDto> getUpcomingExams(User currentUser) {
        Long schoolId = TenantContext.getSchoolId();
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = fromDate.plusDays(7);

        Long classId = null;
        List<Examination> examinations;
        if (currentUser != null && currentUser.hasRole(UserRole.STUDENT)) {
            classId = studentRepository.findByUserId(currentUser.getId())
                    .filter(student -> student.getSchool().getId().equals(schoolId))
                    .map(student -> student.getCurrentClass() == null ? null : student.getCurrentClass().getId())
                    .orElse(null);

            if (classId == null) {
                return List.of();
            }
            examinations = examinationRepository.findUpcomingBySchoolClass(
                    schoolId, classId, fromDate, toDate);
        } else {
            examinations = examinationRepository.findUpcomingBySchool(schoolId, fromDate, toDate);
        }

        Long studentClassId = classId;
        return examinations.stream()
                .map(examination -> toUpcomingExam(examination, studentClassId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BirthdayDto> getBirthdays() {
        Long schoolId = TenantContext.getSchoolId();
        LocalDate today = LocalDate.now();
        LocalDate windowEnd = today.plusDays(6);
        MonthDay start = MonthDay.from(today);
        MonthDay end = MonthDay.from(windowEnd);

        List<BirthdayDto> birthdays = new ArrayList<>();
        addStudentBirthdays(birthdays, schoolId, today, start, end);
        addStaffBirthdays(birthdays, schoolId, today, start, end);

        return birthdays.stream()
                .sorted(java.util.Comparator.comparing(BirthdayDto::getDate)
                        .thenComparing(BirthdayDto::getName))
                .toList();
    }

    private void addStudentBirthdays(List<BirthdayDto> birthdays, Long schoolId, LocalDate today,
                                     MonthDay start, MonthDay end) {
        for (int month : birthdayMonths(start, end)) {
            for (Student student : studentRepository.findAllBySchoolIdAndBirthMonth(schoolId, month)) {
                LocalDate birthDate = student.getDateOfBirth().atZone(ZoneOffset.UTC).toLocalDate();
                MonthDay monthDay = MonthDay.from(birthDate);
                if (!isWithinWeek(monthDay, start, end)) continue;

                LocalDate date = birthdayDateInWindow(monthDay, today);
                String className = student.getCurrentClass() == null ? null : student.getCurrentClass().getName();
                birthdays.add(new BirthdayDto(
                        student.getId(),
                        student.getUser().getFullName(),
                        "STUDENT",
                        className,
                        date,
                        date.equals(today)));
            }
        }
    }

    private void addStaffBirthdays(List<BirthdayDto> birthdays, Long schoolId, LocalDate today,
                                   MonthDay start, MonthDay end) {
        for (int month : birthdayMonths(start, end)) {
            for (User user : userRepository.findAllBySchoolIdAndBirthMonth(schoolId, month)) {
                boolean staff = user.getRoles().stream().anyMatch(role -> role == UserRole.SCHOOL_ADMIN
                        || role == UserRole.PRINCIPAL
                        || role == UserRole.TEACHER
                        || role == UserRole.FINANCE_OFFICER);
                if (!staff || user.getDateOfBirth() == null) continue;

                MonthDay monthDay = MonthDay.from(user.getDateOfBirth().atZone(ZoneOffset.UTC).toLocalDate());
                if (!isWithinWeek(monthDay, start, end)) continue;

                LocalDate date = birthdayDateInWindow(monthDay, today);
                birthdays.add(new BirthdayDto(
                        user.getId(), user.getFullName(), "STAFF", null, date, date.equals(today)));
            }
        }
    }

    private List<Integer> birthdayMonths(MonthDay start, MonthDay end) {
        if (start.getMonthValue() == end.getMonthValue()) {
            return List.of(start.getMonthValue());
        }
        return List.of(start.getMonthValue(), end.getMonthValue());
    }

    private boolean isWithinWeek(MonthDay birthday, MonthDay start, MonthDay end) {
        if (!start.isAfter(end)) {
            return !birthday.isBefore(start) && !birthday.isAfter(end);
        }
        return !birthday.isBefore(start) || !birthday.isAfter(end);
    }

    private LocalDate birthdayDateInWindow(MonthDay birthday, LocalDate windowStart) {
        for (int i = 0; i < 7; i++) {
            LocalDate date = windowStart.plusDays(i);
            if (MonthDay.from(date).equals(birthday)) return date;
        }
        throw new IllegalStateException("Birthday is outside the current week");
    }

    private UpcomingExamDto toUpcomingExam(Examination examination, Long classId) {
        String subjects = examination.getSubjectClasses().stream()
                .filter(link -> classId == null || link.getSchoolClass().getId().equals(classId))
                .map(link -> link.getSubject().getName())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", "));

        return new UpcomingExamDto(
                examination.getId(),
                examination.getName(),
                subjects.isBlank() ? "—" : subjects,
                examination.getStartDate(),
                examination.getEndDate());
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

    private double calculateAttendanceRate(Long schoolId, LocalDate date) {
        List<Object[]> results = attendanceRepository.getAttendanceSummary(
                schoolId, date, date, null, AttendanceStatus.PRESENT);

        if (results == null || results.isEmpty() || results.get(0) == null) {
            return 0.0;
        }

        Object[] row = results.get(0);
        long total = row[0] != null ? ((Number) row[0]).longValue() : 0;
        long present = row[1] != null ? ((Number) row[1]).longValue() : 0;
        return total > 0 ? Math.round((present * 10000.0) / total) / 100.0 : 0.0;
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

        LocalDate todayDate = LocalDate.now();
        List<LessonDto> todayLessons = lessonService
                .getOrCreateLessonsForTeacherAndDate(currentUser, currentUser.getId(), todayDate);
        stats.setTodayLessons(todayLessons);
        stats.setTodayPeriods(todayLessons.size());
        stats.setPendingAttendance(todayLessons.stream()
                .filter(lesson -> lesson.getStatus() == LessonStatus.SCHEDULED
                        || lesson.getStatus() == LessonStatus.SUBSTITUTED)
                .count());

        Term currentTerm = getCurrentTerm(schoolId);
        if (currentTerm != null) {
            List<TeacherAssessmentTaskDto> missingMarkTasks = new ArrayList<>();
            for (Assessment assessment : assessmentRepository
                    .findAllByAssignment_Teacher_IdAndSchoolIdOrderByDateDesc(currentUser.getId(), schoolId)) {
                if (!assessment.getTerm().getId().equals(currentTerm.getId())) continue;

                Long classId = assessment.getAssignment().getSchoolClass().getId();
                long activeStudents = studentRepository.countBySchoolIdAndCurrentClassIdAndStatus(
                        schoolId, classId, StudentStatus.ACTIVE);
                long markedStudents = markRepository.countByAssessmentIdAndScoreIsNotNull(assessment.getId());
                long missingMarks = Math.max(0, activeStudents - markedStudents);

                if (missingMarks > 0) {
                    missingMarkTasks.add(new TeacherAssessmentTaskDto(
                            assessment.getId(),
                            assessment.getName(),
                            assessment.getAssignment().getSubject().getName(),
                            assessment.getAssignment().getSchoolClass().getName(),
                            assessment.getDate(),
                            missingMarks));
                }
            }
            stats.setAssessmentsMissingMarks(missingMarkTasks);
            stats.setMissingMarks(missingMarkTasks.stream()
                    .mapToLong(TeacherAssessmentTaskDto::getMissingMarks)
                    .sum());
        }

        return stats;
    }
}
