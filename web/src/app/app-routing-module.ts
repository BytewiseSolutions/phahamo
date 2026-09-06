import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {Login} from './pages/login/login';
import {Register} from './pages/register/register';
import {Dashboard} from './pages/dashboard/dashboard';
import {authGuard} from './util/auth.guard';

const routes: Routes = [
  {path: 'login', component: Login},
  {path: 'register', component: Register},
  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  {path: '', redirectTo: 'login', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
