import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import {ReactiveFormsModule, FormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AuthInterceptor} from './util/auth.interceptor';
import { Dashboard } from './pages/dashboard/dashboard';
import { ForgotPassword } from './pages/forgot-password/forgot-password';
import { ResetPassword } from './pages/reset-password/reset-password';
import { UserForm } from './pages/user/user-form/user-form';
import { UserList } from './pages/user/user-list/user-list';
import { Sidebar } from './components/sidebar/sidebar';
import { Layout } from './components/layout/layout';
import { Header } from './components/header/header';
import { SchoolProfile } from './pages/school-profile/school-profile';
import { AcademicYearList } from './pages/academic-years/academic-year-list/academic-year-list';
import { AcademicYearForm } from './pages/academic-years/academic-year-form/academic-year-form';
import { TermList } from './pages/terms/term-list/term-list';
import { TermForm } from './pages/terms/term-form/term-form';
import { AcademicYearView } from './pages/academic-years/academic-year-view/academic-year-view';
import { ClassList } from './pages/class/class-list/class-list';
import { ClassForm } from './pages/class/class-form/class-form';
import { ClassView } from './pages/class/class-view/class-view';
import { SubjectList } from './pages/subject/subject-list/subject-list';
import { SubjectForm } from './pages/subject/subject-form/subject-form';
import { SubjectClassAssignmentList } from './pages/subject-class-assignment/subject-class-assignment-list/subject-class-assignment-list';
import { SubjectClassAssignmentForm } from './pages/subject-class-assignment/subject-class-assignment-form/subject-class-assignment-form';
import { ClassStudentList } from './pages/class/class-student-list/class-student-list';
import {NgSelectModule} from '@ng-select/ng-select';
import { ConfirmDialog } from './components/confirm-dialog/confirm-dialog';
import { SuccessDialog } from './components/success-dialog/success-dialog';
import { TransferStudentForm } from './pages/class/transfer-student-form/transfer-student-form';
import { ParentLinkForm } from './pages/student/parent-link-form/parent-link-form';
import { TimetablePage } from './pages/timetable/timetable-page/timetable-page';
import { PeriodForm } from './pages/timetable/period-form/period-form';
import { TimetableForm } from './pages/timetable/timetable-form/timetable-form';
import { AttendancePage } from './pages/attendance/attendance-page/attendance-page';
import { AttendanceForm } from './pages/attendance/attendance-form/attendance-form';
import { MyClassesPage } from './pages/my-classes/my-classes-page/my-classes-page';
import { MyClassView } from './pages/my-classes/my-class-view/my-class-view';
import { AuthCallback } from './pages/auth-callback/auth-callback';
import {UnauthorizedInterceptor} from './util/unauthorized.interceptor';
import { UserView } from './pages/user/user-view/user-view';
import { TeacherSubjectList } from './pages/teacher/teacher-subject-list/teacher-subject-list';
import { TeacherTimetable } from './pages/teacher/teacher-timetable/teacher-timetable';
import { TeacherLessons } from './pages/teacher/teacher-lessons/teacher-lessons';
import { StudentParentList } from './pages/student/student-parent-list/student-parent-list';
import { StudentParentForm } from './pages/student/student-parent-form/student-parent-form';
import { SubscriptionList } from './pages/subscription/subscription-list/subscription-list';
import { PlanPriceList } from './pages/plan-price/plan-price-list/plan-price-list';
import { PlanPriceForm } from './pages/plan-price/plan-price-form/plan-price-form';
import { Plans } from './pages/plans/plans';
import { UpgradeBanner } from './components/upgrade-banner/upgrade-banner';
import { PaymentMethodList } from './pages/payment-method/payment-method-list/payment-method-list';
import { PaymentMethodForm } from './pages/payment-method/payment-method-form/payment-method-form';
import { SubscribeForm } from './pages/plans/subscribe-form/subscribe-form';
import { SubscriptionHistory } from './pages/subscription/subscription-history/subscription-history';
import { GradeList } from './pages/grade/grade-list/grade-list';
import { AssessmentForm } from './pages/grade/assessment-form/assessment-form';
import { MarkEntry } from './pages/grade/mark-entry/mark-entry';

@NgModule({
  declarations: [
    App,
    Login,
    Register,
    Dashboard,
    ForgotPassword,
    ResetPassword,
    UserForm,
    UserList,
    Sidebar,
    Layout,
    Header,
    SchoolProfile,
    AcademicYearList,
    AcademicYearForm,
    TermList,
    TermForm,
    AcademicYearView,
    ClassList,
    ClassForm,
    ClassView,
    SubjectList,
    SubjectForm,
    SubjectClassAssignmentList,
    SubjectClassAssignmentForm,
    ClassStudentList,
    ConfirmDialog,
    SuccessDialog,
    TransferStudentForm,
    ParentLinkForm,
    TimetablePage,
    PeriodForm,
    TimetableForm,
    AttendancePage,
    AttendanceForm,
    MyClassesPage,
    MyClassView,
    AuthCallback,
    UserView,
    TeacherSubjectList,
    TeacherTimetable,
    TeacherLessons,
    StudentParentList,
    StudentParentForm,
    SubscriptionList,
    PlanPriceList,
    PlanPriceForm,
    Plans,
    UpgradeBanner,
    PaymentMethodList,
    PaymentMethodForm,
    SubscribeForm,
    SubscriptionHistory,
    GradeList,
    AssessmentForm,
    MarkEntry
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    NgSelectModule
  ],
  providers: [
    provideBrowserGlobalErrorListeners(),
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: UnauthorizedInterceptor, multi: true }
  ],
  bootstrap: [App]
})
export class AppModule { }
