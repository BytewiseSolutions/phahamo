import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { BackendService } from './backend.service';
import {LoginRequest, LoginResponse} from './auth';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private tokenKey = 'sms_token';

  constructor(private backend: BackendService, private router: Router) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.backend.post<LoginResponse>('/auth/login', credentials).pipe(
      tap((response) => {
        localStorage.setItem(this.tokenKey, response.token);
      })
    );
  }

  logout(): void {
    this.backend.post<void>('/auth/logout', {}).subscribe({
      next: () => this.completeLogout(),
      error: () => this.completeLogout()
    });
  }
  private completeLogout(): void {
    localStorage.removeItem(this.tokenKey);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
