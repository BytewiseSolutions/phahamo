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
import { StudentList } from './pages/student/student-list/student-list';
import { StudentForm } from './pages/student/student-form/student-form';
import { ClassStudentList } from './pages/class/class-student-list/class-student-list';
import {NgSelectModule} from '@ng-select/ng-select';
import { ConfirmDialog } from './components/confirm-dialog/confirm-dialog';
import { SuccessDialog } from './components/success-dialog/success-dialog';
import { TransferStudentForm } from './pages/class/transfer-student-form/transfer-student-form';
import { StudentView } from './pages/student/student-view/student-view';
import { ParentList } from './pages/parent/parent-list/parent-list';
import { ParentForm } from './pages/parent/parent-form/parent-form';
import { ParentLinkForm } from './pages/parent/parent-link-form/parent-link-form';
import { TeacherList } from './pages/teacher/teacher-list/teacher-list';
import { TeacherForm } from './pages/teacher/teacher-form/teacher-form';
import { TeacherView } from './pages/teacher/teacher-view/teacher-view';
import { TimetablePage } from './pages/timetable/timetable-page/timetable-page';
import { PeriodForm } from './pages/timetable/period-form/period-form';
import { TimetableForm } from './pages/timetable/timetable-form/timetable-form';
import { AttendancePage } from './pages/attendance/attendance-page/attendance-page';
import { AttendanceForm } from './pages/attendance/attendance-form/attendance-form';
import { MyClassesPage } from './pages/my-classes/my-classes-page/my-classes-page';
import { MyClassView } from './pages/my-classes/my-class-view/my-class-view';
import { AuthCallback } from './pages/auth-callback/auth-callback';
import {UnauthorizedInterceptor} from './util/unauthorized.interceptor';

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
    StudentList,
    StudentForm,
    ClassStudentList,
    ConfirmDialog,
    SuccessDialog,
    TransferStudentForm,
    StudentView,
    ParentList,
    ParentForm,
    ParentLinkForm,
    TeacherList,
    TeacherForm,
    TeacherView,
    TimetablePage,
    PeriodForm,
    TimetableForm,
    AttendancePage,
    AttendanceForm,
    MyClassesPage,
    MyClassView,
    AuthCallback
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
