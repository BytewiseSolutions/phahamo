import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../util/auth.service';
import { BackendService } from '../../util/backend.service';
import { Router } from '@angular/router';
import { Subscription } from '../../util/subscription';
import { Announcement } from '../announcements/announcement';
import { DashboardStats } from './dashboard-stats';
import { StudentDashboardStats } from './student-dashboard-stats';
import { EChartsOption } from 'echarts';
import {TeacherDashboardStats} from './teacher-dashboard-stats';
import {SystemDashboardStats} from './system-dashboard-stats';
import { StudentFeePayment } from '../student-fees/student-fee-payment';
import { UpcomingExam } from './upcoming-exam';
import { Birthday } from './birthday';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {
  subscription: Subscription | null = null;
  announcements: Announcement[] = [];

  stats: DashboardStats | null = null;
  isAdmin = false;

  studentStats: StudentDashboardStats | null = null;
  isStudent = false;
  teacherStats: TeacherDashboardStats | null = null;
  isTeacher = false;
  isStaff = false;

  systemStats: SystemDashboardStats | null = null;
  isSystemAdmin = false;
  selectedSchoolId: number | null = null;
  schoolsForPicker: { schoolId: number; schoolName: string }[] = [];

  adminAttendanceLineOptions: EChartsOption = {};
  adminFeePieOptions: EChartsOption = {};
  studentAttendanceLineOptions: EChartsOption = {};
  recentPayments: StudentFeePayment[] = [];
  recentPaymentsLoading = false;
  upcomingExams: UpcomingExam[] = [];
  upcomingExamsLoaded = false;
  birthdays: Birthday[] = [];
  birthdaysLoaded = false;

  constructor(
    private authService: AuthService,
    private backend: BackendService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.hasRole('SCHOOL_ADMIN')
      || this.authService.hasRole('SYSTEM_ADMIN')
      || this.authService.hasRole('PRINCIPAL');

    this.isSystemAdmin = this.authService.hasRole('SYSTEM_ADMIN');
    this.isStudent = this.authService.hasRole('STUDENT');
    this.isTeacher = this.authService.hasRole('TEACHER');
    this.isStaff = this.isAdmin || this.isTeacher || this.authService.hasRole('FINANCE_OFFICER');

    if (this.isSystemAdmin) {
      this.loadSystemStats();
      this.loadSchoolsForPicker();
    } else if (this.isAdmin) {
      this.backend.get<DashboardStats>('/dashboard/stats').subscribe({
        next: (data) => this.stats = data
      });
      this.loadAdminCharts();
      this.loadRecentPayments();
      this.loadUpcomingExams();
      this.loadBirthdays();
    }

    if (this.isStudent) {
      this.backend.get<StudentDashboardStats>('/dashboard/student').subscribe({
        next: (data) => this.studentStats = data
      });
      this.loadStudentChart();
      this.loadUpcomingExams();
      this.loadBirthdays();
    }

    if (!this.isSystemAdmin) {
      this.backend.get<Subscription>('/subscriptions/me').subscribe({
        next: (sub) => this.subscription = sub,
        error: () => {}
      });
    }

    this.backend.get<Announcement[]>('/announcements/feed').subscribe({
      next: (data) => this.announcements = data
    });

    if (this.isTeacher) {
      this.backend.get<TeacherDashboardStats>('/dashboard/teacher').subscribe({
        next: (data) => this.teacherStats = data,
        error: () => this.teacherStats = null
      });
    }

    if (this.isStaff && !this.isAdmin && !this.isStudent) {
      this.loadBirthdays();
    }
  }

  private loadSystemStats(): void {
    this.backend.get<SystemDashboardStats>('/dashboard/system').subscribe({
      next: (data) => this.systemStats = data
    });
  }

  private loadSchoolsForPicker(): void {
    this.backend.get<any[]>('/admin/schools').subscribe({
      next: (data) => {
        this.schoolsForPicker = data.map(s => ({
          schoolId: s.schoolId,
          schoolName: s.schoolName
        }));
      }
    });
  }

  onSchoolSelected(): void {
    if (this.selectedSchoolId === null) {
      this.stats = null;
      this.announcements = [];
      this.adminAttendanceLineOptions = {};
      this.adminFeePieOptions = {};
      this.recentPayments = [];
      this.upcomingExams = [];
      this.upcomingExamsLoaded = false;
      this.birthdays = [];
      this.birthdaysLoaded = false;
      localStorage.removeItem('activeSchoolId');
      return;
    }

    localStorage.setItem('activeSchoolId', String(this.selectedSchoolId));

    this.stats = null;
    this.announcements = [];
    this.adminAttendanceLineOptions = {};
    this.adminFeePieOptions = {};

    this.backend.get<DashboardStats>('/dashboard/stats').subscribe({
      next: (data) => this.stats = data
    });
    this.loadAdminCharts();
    this.loadRecentPayments();
    this.loadUpcomingExams();
    this.loadBirthdays();

    this.backend.get<Announcement[]>('/announcements/feed').subscribe({
      next: (data) => this.announcements = data
    });
  }

  private loadRecentPayments(): void {
    if (this.isSystemAdmin && this.selectedSchoolId === null) return;

    this.recentPaymentsLoading = true;
    this.backend.get<StudentFeePayment[]>('/dashboard/recent-payments').subscribe({
      next: (payments) => {
        this.recentPayments = payments;
        this.recentPaymentsLoading = false;
      },
      error: () => {
        this.recentPayments = [];
        this.recentPaymentsLoading = false;
      }
    });
  }

  private loadUpcomingExams(): void {
    if (this.isSystemAdmin && this.selectedSchoolId === null) return;
    if (!this.isAdmin && !this.isStudent) return;

    this.upcomingExamsLoaded = false;
    this.backend.get<UpcomingExam[]>('/dashboard/upcoming-exams').subscribe({
      next: (exams) => {
        this.upcomingExams = exams;
        this.upcomingExamsLoaded = true;
      },
      error: () => {
        this.upcomingExams = [];
        this.upcomingExamsLoaded = true;
      }
    });
  }

  private loadBirthdays(): void {
    if (!this.isStaff) return;
    if (this.isSystemAdmin && this.selectedSchoolId === null) return;

    this.birthdaysLoaded = false;
    this.backend.get<Birthday[]>('/dashboard/birthdays').subscribe({
      next: (birthdays) => {
        this.birthdays = birthdays;
        this.birthdaysLoaded = true;
      },
      error: () => {
        this.birthdays = [];
        this.birthdaysLoaded = true;
      }
    });
  }

  private loadAdminCharts(): void {
    this.backend.get<{ labels: string[], data: number[] }>('/dashboard/admin-attendance-chart').subscribe({
      next: (res) => {
        this.adminAttendanceLineOptions = {
          title: { text: 'Weekly Attendance Trend', left: 'center' },
          tooltip: { trigger: 'axis' },
          xAxis: { type: 'category', data: res.labels },
          yAxis: { type: 'value', min: 0, max: 100, axisLabel: { formatter: '{value} %' } },
          series: [{
            name: 'Attendance Rate',
            type: 'line',
            data: res.data,
            smooth: true,
            itemStyle: { color: '#2563eb' }
          }]
        };
      }
    });

    this.backend.get<{ expected: number, paid: number }>('/dashboard/admin-fee-chart').subscribe({
      next: (res) => {
        const outstanding = Math.max(0, res.expected - res.paid);
        this.adminFeePieOptions = {
          title: { text: 'Fee Collection Status', left: 'center' },
          tooltip: { trigger: 'item', formatter: '{b}: M {c} ({d}%)' },
          legend: { bottom: 0 },
          series: [
            {
              name: 'Fees',
              type: 'pie',
              radius: ['40%', '70%'],
              avoidLabelOverlap: false,
              itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
              label: { show: false, position: 'center' },
              emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
              labelLine: { show: false },
              data: [
                { value: res.paid, name: 'Collected', itemStyle: { color: '#10b981' } },
                { value: outstanding, name: 'Outstanding', itemStyle: { color: '#ef4444' } }
              ]
            }
          ]
        };
      }
    });
  }

  private loadStudentChart(): void {
    this.backend.get<{ labels: string[], data: number[] }>('/dashboard/student-attendance-chart').subscribe({
      next: (res) => {
        this.studentAttendanceLineOptions = {
          title: { text: 'My Attendance Trend', left: 'center' },
          tooltip: { trigger: 'axis' },
          xAxis: { type: 'category', data: res.labels },
          yAxis: { type: 'value', min: 0, max: 100, axisLabel: { formatter: '{value} %' } },
          series: [{
            name: 'My Attendance',
            type: 'line',
            data: res.data,
            smooth: true,
            itemStyle: { color: '#10b981' }
          }]
        };
      }
    });
  }

  goToTimetable(): void { this.router.navigate(['/timetable']); }
  goToAttendance(): void { this.router.navigate(['/attendance/my']); }
  goToResults(): void { this.router.navigate(['/results/my']); }
  goToFees(): void { this.router.navigate(['/my-fees']); }
  goToPaymentHistory(): void { this.router.navigate(['/student-fees/payments']); }
  goToExam(examId: number): void { this.router.navigate(['/examinations', examId]); }
  goToExaminations(): void { this.router.navigate(['/examinations']); }
  goToUsers(): void { this.router.navigate(['/users']); }
  goToAttendanceHistory(): void { this.router.navigate(['/attendance/history']); }
  goToGrades(): void { this.router.navigate(['/grades']); }
  goToAssessment(assessmentId: number): void { this.router.navigate(['/grades', assessmentId, 'marks']); }
  goToTeacherAttendance(): void { this.router.navigate(['/my-classes']); }
  goToAnnouncements(): void { this.router.navigate(['/announcements']); }
  goToMyClasses(): void { this.router.navigate(['/my-classes']); }

  get showUpcomingExams(): boolean {
    return this.upcomingExamsLoaded
      && (this.isStudent || (this.isAdmin && (!this.isSystemAdmin || this.selectedSchoolId !== null)));
  }

  get showBirthdays(): boolean {
    return this.birthdaysLoaded && this.isStaff
      && (!this.isSystemAdmin || this.selectedSchoolId !== null);
  }
}
