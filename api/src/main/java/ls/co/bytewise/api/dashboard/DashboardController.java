package ls.co.bytewise.api.dashboard;

import ls.co.bytewise.api.user.User;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public DashboardStatsDto getAdminStats() {
        return dashboardService.getAdminStats();
    }

    @GetMapping("/student")
    @Secured({"ROLE_STUDENT"})
    public StudentDashboardStatsDto getStudentStats(@AuthenticationPrincipal User currentUser) {
        return dashboardService.getStudentStats(currentUser);
    }

    @GetMapping("/admin-attendance-chart")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public ChartDataDto getAdminAttendanceChart() {
        return dashboardService.getAdminAttendanceChart();
    }

    @GetMapping("/admin-fee-chart")
    @Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL"})
    public FeeChartDto getAdminFeeChart() {
        return dashboardService.getAdminFeeChart();
    }

    @GetMapping("/student-attendance-chart")
    @Secured({"ROLE_STUDENT"})
    public ChartDataDto getStudentAttendanceChart(@AuthenticationPrincipal User currentUser) {
        return dashboardService.getStudentAttendanceChart(currentUser);
    }

    @GetMapping("/teacher")
    @Secured({"ROLE_TEACHER"})
    public TeacherDashboardStatsDto getTeacherStats(@AuthenticationPrincipal User currentUser) {
        return dashboardService.getTeacherStats(currentUser);
    }
}