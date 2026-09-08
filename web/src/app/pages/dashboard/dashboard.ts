import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../util/auth.service';
import { BackendService } from '../../util/backend.service';
import { ChangePasswordRequest } from '../../util/auth';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard {
  changePasswordForm: FormGroup;
  submitted = false;
  errorMessage = '';
  successMessage = '';
  showChangePassword = false;

  constructor(
    private authService: AuthService,
    private backend: BackendService,
    private fb: FormBuilder
  ) {
    this.changePasswordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required]
    });
  }

  onLogout(): void {
    this.authService.logout();
  }

  toggleChangePassword(): void {
    this.showChangePassword = !this.showChangePassword;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onChangePassword(): void {
    this.submitted = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.changePasswordForm.invalid) {
      this.changePasswordForm.markAllAsTouched();
      return;
    }

    const { currentPassword, newPassword, confirmPassword } = this.changePasswordForm.value;

    if (newPassword !== confirmPassword) {
      this.errorMessage = 'New passwords do not match.';
      return;
    }

    const payload: ChangePasswordRequest = { currentPassword, newPassword };

    this.backend.post<void>('/auth/change-password', payload).subscribe({
      next: () => {
        this.successMessage = 'Password changed successfully.';
        this.changePasswordForm.reset();
      },
      error: (err) => {
        this.errorMessage = err.status === 401
          ? 'Current password is incorrect.'
          : 'Something went wrong. Please try again.';
      }
    });
  }
}
