import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { BackendService } from './backend.service';
import { LoginRequest, LoginResponse } from './auth';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private tokenKey = 'sms_token';
  private schoolIdKey = 'sms_school_id';

  constructor(private backend: BackendService, private router: Router) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.backend.post<LoginResponse>('/auth/login', credentials).pipe(
      tap((response) => {
        localStorage.setItem(this.tokenKey, response.token);

        if (response.schoolIds && response.schoolIds.length > 0) {
          localStorage.setItem(this.schoolIdKey, String(response.schoolIds[0]));
        }
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
    localStorage.removeItem(this.schoolIdKey);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getActiveSchoolId(): string | null {
    return localStorage.getItem(this.schoolIdKey);
  }

  setActiveSchoolId(schoolId: number): void {
    localStorage.setItem(this.schoolIdKey, String(schoolId));
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
