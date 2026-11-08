import { Component, OnInit } from '@angular/core';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../../util/auth.service';
import { BackendService } from '../../util/backend.service';
import { Router } from '@angular/router';
import { Subscription } from '../../util/subscription';
import { Announcement } from '../announcements/announcement';
import { DashboardStats } from './dashboard-stats';
import { StudentDashboardStats } from './student-dashboard-stats';
import { EChartsOption } from 'echarts';
import { TeacherDashboardStats } from './teacher-dashboard-stats';
import { SystemDashboardStats } from './system-dashboard-stats';
import { StudentFeePayment } from '../student-fees/student-fee-payment';
import { UpcomingExam } from './upcoming-exam';
import { Birthday } from './birthday';
import { MessageUnreadService } from '../../util/message-unread.service';
import { FinanceDashboardStats } from './finance-dashboard-stats';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {
  setupChecklistVisible = false;
  setupChecklistLoading = false;
  setupChecklist = [
    { key: 'profile', label: 'Complete school profile', path: '/school-profile', done: false },
    { key: 'academicYear', label: 'Create an academic year', path: '/academic-years', done: false },
    { key: 'terms', label: 'Add terms', path: '/academic-years', done: false },
    { key: 'classes', label: 'Create classes', path: '/classes', done: false },
    { key: 'subjects', label: 'Add subjects', path: '/subjects', done: false },
    { key: 'teachers', label: 'Add teachers', path: '/users', done: false },
    { key: 'students', label: 'Add students', path: '/users', done: false },
    { key: 'periods', label: 'Set lesson periods', path: '/lessons/range', done: false },
    { key: 'timetable', label: 'Create the timetable', path: '/timetable', done: false },
    { key: 'fees', label: 'Set up fee structures', path: '/fees/structures', done: false }
  ];

  subscription: Subscription | null = null;
  announcements: Announcement[] = [];

  stats: DashboardStats | null = null;
  isAdmin = false;
  isSchoolAdmin = false;

  studentStats: StudentDashboardStats | null = null;
  isStudent = false;
  teacherStats: TeacherDashboardStats | null = null;
  isTeacher = false;
  isFinanceOfficer = false;
  isParent = false;
  financeStats: FinanceDashboardStats | null = null;
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
  unreadMessages = 0;

  constructor(
    private authService: AuthService,
    private backend: BackendService,
    private router: Router,
    private messageUnreadService: MessageUnreadService
  ) { }

  ngOnInit(): void {
    this.isAdmin = this.authService.hasRole('SCHOOL_ADMIN')
      || this.authService.hasRole('SYSTEM_ADMIN')
      || this.authService.hasRole('PRINCIPAL');
    this.isSchoolAdmin = this.authService.hasRole('SCHOOL_ADMIN');

    this.isSystemAdmin = this.authService.hasRole('SYSTEM_ADMIN');
    this.isStudent = this.authService.hasRole('STUDENT');
    this.isTeacher = this.authService.hasRole('TEACHER');
    this.isFinanceOfficer = this.authService.hasRole('FINANCE_OFFICER');
    this.isParent = this.authService.hasRole('PARENT');
    this.isStaff = this.isAdmin || this.isTeacher || this.authService.hasRole('FINANCE_OFFICER');

    if (this.isParent) {
      this.router.navigate(['/parent/children']);
      return;
    }

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

    if (this.authService.hasRole('SCHOOL_ADMIN')) {
      this.setupChecklistVisible = localStorage.getItem('phahamo_setup_checklist_hidden') !== 'true';
      this.loadSetupChecklist();
    }

    if (this.isStudent) {
      this.backend.get<StudentDashboardStats>('/dashboard/student').subscribe({
        next: (data) => this.studentStats = data
      });
      this.loadStudentChart();
      this.loadUpcomingExams();
      this.loadBirthdays();
    }

    if (this.isFinanceOfficer) {
      this.backend.get<FinanceDashboardStats>('/dashboard/finance').subscribe({
        next: (data) => this.financeStats = data
      });
      this.loadRecentPayments();
    }

    if (!this.isSystemAdmin && !this.isParent) {
      this.backend.get<Subscription>('/subscriptions/me').subscribe({
        next: (sub) => this.subscription = sub,
        error: () => { }
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

    if (this.isStaff || this.isStudent) {
      this.messageUnreadService.unread$.subscribe(count => this.unreadMessages = count);
      this.messageUnreadService.refresh();
    }
  }

  get setupChecklistCompleted(): number {
    return this.setupChecklist.filter(step => step.done).length;
  }

  hideSetupChecklist(): void {
    this.setupChecklistVisible = false;
    localStorage.setItem('phahamo_setup_checklist_hidden', 'true');
  }

  showSetupChecklist(): void {
    this.setupChecklistVisible = true;
    localStorage.removeItem('phahamo_setup_checklist_hidden');
    this.loadSetupChecklist();
  }

  goToSetupStep(path: string): void {
    this.router.navigate([path]);
  }

  private loadSetupChecklist(): void {
    const schoolId = this.authService.getActiveSchoolId();
    if (!schoolId) return;

    this.setupChecklistLoading = true;
    forkJoin({
      profile: this.backend.get<any>(`/schools/${schoolId}`).pipe(catchError(() => of(null))),
      academicYears: this.backend.get<any[]>('/academic-years').pipe(catchError(() => of([]))),
      terms: this.backend.get<any[]>('/terms').pipe(catchError(() => of([]))),
      classes: this.backend.get<any[]>('/classes').pipe(catchError(() => of([]))),
      subjects: this.backend.get<any[]>('/subjects').pipe(catchError(() => of([]))),
      users: this.backend.get<any[]>('/users').pipe(catchError(() => of([]))),
      periods: this.backend.get<any[]>('/periods').pipe(catchError(() => of([]))),
      timetable: this.backend.get<any[]>('/timetable').pipe(catchError(() => of([]))),
      fees: this.backend.get<any[]>('/fee-structures').pipe(catchError(() => of([])))
    }).subscribe(data => {
      const profile = data.profile;
      this.setSetupStep('profile', !!profile?.schoolName && !!profile?.schoolContactEmail
        && !!profile?.schoolAddress && !!profile?.schoolContactPhone);
      this.setSetupStep('academicYear', data.academicYears.length > 0);
      this.setSetupStep('terms', data.terms.length > 0);
      this.setSetupStep('classes', data.classes.length > 0);
      this.setSetupStep('subjects', data.subjects.length > 0);
      this.setSetupStep('teachers', data.users.some(user => user.roles?.includes('TEACHER')));
      this.setSetupStep('students', data.users.some(user => user.roles?.includes('STUDENT')));
      this.setSetupStep('periods', data.periods.length > 0);
      this.setSetupStep('timetable', data.timetable.length > 0);
      this.setSetupStep('fees', data.fees.length > 0);
      this.setupChecklistLoading = false;
    });
  }

  private setSetupStep(key: string, done: boolean): void {
    const step = this.setupChecklist.find(item => item.key === key);
    if (step) step.done = done;
  }

  private loadSystemStats(): void {
    this.backend.get<SystemDashboardStats>('/dashboard/system').subscribe({
      next: (data) => this.systemStats = data
    });
  }

  goToSystemSchools(filter?: string, status?: string): void {
    this.router.navigate(['/admin/schools'], {
      queryParams: {
        ...(filter ? { filter } : {}),
        ...(status ? { status } : {})
      }
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
  goToFeeAccounts(): void { this.router.navigate(['/student-fees']); }
  goToExam(examId: number): void { this.router.navigate(['/examinations', examId]); }
  goToExaminations(): void { this.router.navigate(['/examinations']); }
  goToUsers(): void { this.router.navigate(['/users']); }
  goToAttendanceHistory(): void { this.router.navigate(['/attendance/history']); }
  goToGrades(): void { this.router.navigate(['/grades']); }
  goToAssessment(assessmentId: number): void { this.router.navigate(['/grades', assessmentId, 'marks']); }
  goToTeacherAttendance(): void { this.router.navigate(['/my-classes']); }
  goToAnnouncements(): void { this.router.navigate(['/announcements']); }
  goToMessages(): void { this.router.navigate(['/messages']); }
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
