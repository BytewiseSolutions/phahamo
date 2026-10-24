import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const systemAdminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.getRoles().includes('SYSTEM_ADMIN')) {
    return true;
  }

  router.navigate(['/dashboard']);
  return false;
};
