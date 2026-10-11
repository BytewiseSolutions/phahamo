import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../util/auth.service';
import { BackendService } from '../../util/backend.service';
import { ChangePasswordRequest } from '../../util/auth';
import { Router } from '@angular/router';
import { Subscription } from '../../util/subscription';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {
  changePasswordForm: FormGroup;
  submitted = false;
  errorMessage = '';
  successMessage = '';
  showChangePassword = false;

  subscription: Subscription | null = null;

  constructor(
    private authService: AuthService,
    private backend: BackendService,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.changePasswordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.backend.get<Subscription>('/subscriptions/me').subscribe({
      next: (sub) => this.subscription = sub,
      error: () => this.subscription = null
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

  protected openUsers(): void {
    this.router.navigate(['/users']);
  }
}
