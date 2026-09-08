import { Injectable } from '@angular/core';
import {HttpRequest, HttpHandler, HttpEvent, HttpInterceptor} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();
    const schoolId = this.authService.getActiveSchoolId();

    let headers: { [key: string]: string } = {};

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    if (schoolId) {
      headers['X-School-Id'] = schoolId;
    }

    if (Object.keys(headers).length > 0) {
      const cloned = req.clone({ setHeaders: headers });
      return next.handle(cloned);
    }

    return next.handle(req);
  }
}
