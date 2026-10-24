import { Injectable } from '@angular/core';
import {
  HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable()
export class SubscriptionInterceptor implements HttpInterceptor {

  constructor(private router: Router, private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 403 && error.error?.code === 'SUBSCRIPTION_EXPIRED') {
          const roles = this.authService.getRoles();
          const isPrivileged = roles.includes('SCHOOL_ADMIN') || roles.includes('SYSTEM_ADMIN');
          if (!isPrivileged && !this.router.url.startsWith('/subscription-expired')) {
            this.router.navigate(['/subscription-expired']);
          }
        }
        return throwError(() => error);
      })
    );
  }
}
