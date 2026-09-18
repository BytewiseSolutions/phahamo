import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { BackendService } from './backend.service';
import { LoginRequest, LoginResponse, JwtPayload } from './auth';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private tokenKey = 'sms_token';
  private schoolIdKey = 'sms_school_id';
  private fullNameKey = 'sms_full_name';
  private schoolNameKey = 'sms_school_name';
  private userIdKey = 'sms_user_id';

  constructor(private backend: BackendService, private router: Router) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.backend.post<LoginResponse>('/auth/login', credentials).pipe(
      tap((response) => {
        localStorage.setItem(this.tokenKey, response.token);
        localStorage.setItem(this.userIdKey, String(response.userId));
        localStorage.setItem(this.fullNameKey, response.fullName);
        localStorage.setItem(this.schoolNameKey, response.schoolName ?? '');

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
    localStorage.removeItem(this.userIdKey);
    localStorage.removeItem(this.schoolIdKey);
    localStorage.removeItem(this.fullNameKey);
    localStorage.removeItem(this.schoolNameKey);
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

  private decodeToken(token: string): JwtPayload | null {
    try {
      const payload = token.split('.')[1];
      const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
      const json = decodeURIComponent(
        atob(normalized)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(json);
    } catch {
      return null;
    }
  }

  getUserId(): number | null {
    const id = localStorage.getItem(this.userIdKey);
    return id ? Number(id) : null;
  }

  getFullName(): string {
    return localStorage.getItem(this.fullNameKey) ?? '';
  }

  getSchoolName(): string {
    return localStorage.getItem(this.schoolNameKey) ?? '';
  }

  getRoles(): string[] {
    const token = this.getToken();
    if (!token) return [];
    const payload = this.decodeToken(token);
    return payload?.roles ?? [];
  }

  hasRole(role: string): boolean {
    return this.getRoles().includes(role);
  }
}
