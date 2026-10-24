import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Dashboard } from './pages/dashboard/dashboard';
import { authGuard } from './util/auth.guard';
import { ForgotPassword } from './pages/forgot-password/forgot-password';
import { ResetPassword } from './pages/reset-password/reset-password';
import { UserList } from './pages/user/user-list/user-list';
import { Layout } from './components/layout/layout';
import { SchoolProfile } from './pages/school-profile/school-profile';
import {AcademicYearList} from './pages/academic-years/academic-year-list/academic-year-list';
import {ClassList} from './pages/class/class-list/class-list';
import {SubjectList} from './pages/subject/subject-list/subject-list';
import {TimetablePage} from './pages/timetable/timetable-page/timetable-page';
import {AttendancePage} from './pages/attendance/attendance-page/attendance-page';
import {MyClassesPage} from './pages/my-classes/my-classes-page/my-classes-page';
import {MyClassView} from './pages/my-classes/my-class-view/my-class-view';
import {AuthCallback} from './pages/auth-callback/auth-callback';
import {UserView} from './pages/user/user-view/user-view';
import {SubscriptionList} from './pages/subscription/subscription-list/subscription-list';
import { PlanPriceList } from "./pages/plan-price/plan-price-list/plan-price-list";
import {Plans} from './pages/plans/plans';
import {PaymentMethodList} from './pages/payment-method/payment-method-list/payment-method-list';
import {GradeList} from './pages/grade/grade-list/grade-list';
import {MarkEntry} from './pages/grade/mark-entry/mark-entry';
import {ExaminationList} from './pages/examination/examination-list/examination-list';
import {ExaminationDetail} from './pages/examination/examination-detail/examination-detail';
import {GradeBandList} from './pages/grading/grade-band-list/grade-band-list';
import {ClassResults} from './pages/results/class-results/class-results';
import {AttendanceHistory} from './pages/attendance/attendance-history/attendance-history';
import {MyAttendance} from './pages/attendance/my-attendance/my-attendance';
import {MyResults} from './pages/results/my-results/my-results';
import {FeesPage} from './pages/fees/fees-page/fees-page';
import {StudentFeesPage} from './pages/student-fees/student-fees-page/student-fees-page';
import {StudentFeeDetail} from './pages/student-fees/student-fee-detail/student-fee-detail';
import {MyFees} from './pages/student-fees/my-fees/my-fees';
import {PaymentHistory} from './pages/student-fees/payment-history/payment-history';
import {AnnouncementList} from './pages/announcements/announcement-list/announcement-list';
import {ReportsPage} from './pages/reports/reports-page/reports-page';
import {LessonRangePage} from './pages/lessons/lesson-range-page/lesson-range-page';
import {RemarksPage} from './pages/remarks/remarks-page/remarks-page';
import {PromotionPage} from './pages/promotion/promotion-page/promotion-page';
import {PromotionHistory} from './pages/promotion/promotion-history/promotion-history';
import {EmailReportCards} from './pages/reports/email-report-cards/email-report-cards';
import {BulkImport} from './pages/imports/bulk-import/bulk-import';
import {SubscriptionExpired} from './pages/subscription/subscription-expired/subscription-expired';
import {AdminSchoolList} from './pages/subscription/admin-school-list/admin-school-list';
import {systemAdminGuard} from './util/system-admin.guard';
import {Profile} from './pages/profile/profile/profile';
import {MessagesPage} from './pages/messages/messages-page/messages-page';

const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'forgot-password', component: ForgotPassword },
  { path: 'reset-password', component: ResetPassword },
  { path: 'auth/callback', component: AuthCallback },
  { path: 'subscription-expired', component: SubscriptionExpired },
  {
    path: '',
    component: Layout,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: Dashboard },
      { path: 'users', component: UserList },
      { path: 'import', component: BulkImport },
      { path: 'users/:id', component: UserView },
      { path: 'school-profile', component: SchoolProfile },
      { path: 'academic-years', component: AcademicYearList },
      { path: 'classes', component: ClassList },
      { path: 'subjects', component: SubjectList },
      { path: 'timetable', component: TimetablePage },
      { path: 'my-classes', component: MyClassesPage },
      { path: 'my-classes/:assignmentId', component: MyClassView },
      { path: 'my-classes/:assignmentId/attendance/:lessonId', component: AttendancePage },
      { path: 'subscriptions', component: SubscriptionList },
      { path: 'admin/schools', component: AdminSchoolList, canActivate: [systemAdminGuard] },
      { path: 'plan-prices', component: PlanPriceList },
      { path: 'plans', component: Plans },
      { path: 'payment-methods', component: PaymentMethodList },
      { path: 'grades', component: GradeList },
      { path: 'grades/:id/marks', component: MarkEntry },
      { path: 'examinations', component: ExaminationList },
      { path: 'examinations/:id', component: ExaminationDetail },
      { path: 'grading-scale', component: GradeBandList },
      { path: 'results', component: ClassResults },
      { path: 'report-cards/email', component: EmailReportCards },
      { path: 'attendance/history', component: AttendanceHistory },
      { path: 'attendance/my', component: MyAttendance },
      { path: 'results/my', component: MyResults },
      { path: 'fees/structures', component: FeesPage },
      { path: 'student-fees', component: StudentFeesPage },
      { path: 'student-fees/payments', component: PaymentHistory },
      { path: 'student-fees/:accountId', component: StudentFeeDetail },
      { path: 'my-fees', component: MyFees },
      { path: 'announcements', component: AnnouncementList },
      { path: 'reports', component: ReportsPage },
      { path: 'lessons/range', component: LessonRangePage },
      { path: 'remarks', component: RemarksPage },
      { path: 'students/promote', component: PromotionPage },
      { path: 'students/promotions/history', component: PromotionHistory },
      { path: 'profile', component: Profile },
      { path: 'messages', component: MessagesPage },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
