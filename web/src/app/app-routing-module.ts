import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {Login} from './pages/login/login';
import {Register} from './pages/register/register';
import {Dashboard} from './pages/dashboard/dashboard';
import {authGuard} from './util/auth.guard';
import {ForgotPassword} from './pages/forgot-password/forgot-password';
import {ResetPassword} from './pages/reset-password/reset-password';

const routes: Routes = [
  {path: 'login', component: Login},
  {path: 'register', component: Register},
  { path: 'forgot-password', component: ForgotPassword },
  { path: 'reset-password', component: ResetPassword },
  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  {path: '', redirectTo: 'login', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
