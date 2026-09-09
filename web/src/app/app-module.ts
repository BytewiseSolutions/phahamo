import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import {ReactiveFormsModule} from '@angular/forms';
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
    AcademicYearView
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [
    provideBrowserGlobalErrorListeners(),
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [App]
})
export class AppModule { }
