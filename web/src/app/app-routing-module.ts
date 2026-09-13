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
import {StudentList} from './pages/student/student-list/student-list';

const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'forgot-password', component: ForgotPassword },
  { path: 'reset-password', component: ResetPassword },
  {
    path: '',
    component: Layout,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: Dashboard },
      { path: 'users', component: UserList },
      { path: 'school-profile', component: SchoolProfile },
      { path: 'academic-years', component: AcademicYearList},
      { path: 'classes', component: ClassList},
      { path: 'subjects', component: SubjectList},
      { path: 'students', component: StudentList},
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
