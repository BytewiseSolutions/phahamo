import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BackendService } from '../../../util/backend.service';
import { AuthService } from '../../../util/auth.service';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import { User } from '../../../util/user';
import { ProfileUpdateRequest } from '../profile-update-request';
import { ChangePasswordRequest } from '../change-password-request';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})
export class Profile implements OnInit {

  loading = true;
  saving = false;
  errorMessage = '';
  successMessage = '';

  user: User | null = null;
  form!: FormGroup;

  changePasswordForm!: FormGroup;
  showChangePassword = false;
  changingPassword = false;
  changePasswordError = '';

  constructor(
    private fb: FormBuilder,
    private backend: BackendService,
    private authService: AuthService,
    private confirmDialog: ConfirmDialogService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      phone: ['']
    });

    this.changePasswordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    });

    this.loadProfile();
  }

  private loadProfile(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<User>('/users/me').subscribe({
      next: (data) => {
        this.user = data;
        this.form.patchValue({
          firstName: data.firstName,
          lastName: data.lastName,
          phone: data.phone ?? ''
        });
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load your profile.';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (!this.user) return;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const payload: ProfileUpdateRequest = this.form.value;

    this.backend.putSilently<User>('/users/me', payload).subscribe({
      next: (updated) => {
        this.user = updated;
        this.saving = false;
        this.successMessage = 'Profile updated successfully.';

        localStorage.setItem('sms_full_name',
          `${updated.firstName} ${updated.lastName}`);

        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update profile.';
        this.saving = false;
      }
    });
  }

  openChangePassword(): void {
    this.changePasswordForm.reset();
    this.changePasswordError = '';
    this.showChangePassword = true;
  }

  closeChangePassword(): void {
    if (this.changingPassword) return;
    this.showChangePassword = false;
    this.changePasswordForm.reset();
    this.changePasswordError = '';
  }

  submitChangePassword(): void {
    if (this.changePasswordForm.invalid) {
      this.changePasswordForm.markAllAsTouched();
      return;
    }

    const { currentPassword, newPassword, confirmPassword } = this.changePasswordForm.value;

    if (newPassword !== confirmPassword) {
      this.changePasswordError = 'New passwords do not match.';
      return;
    }

    this.changingPassword = true;
    this.changePasswordError = '';

    const payload: ChangePasswordRequest = { currentPassword, newPassword };

    this.backend.postSilently<void>('/auth/change-password', payload).subscribe({
      next: () => {
        this.changingPassword = false;
        this.showChangePassword = false;
        this.changePasswordForm.reset();
        this.successMessage = 'Password changed successfully.';
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err) => {
        this.changePasswordError = err.error?.message || 'Failed to change password.';
        this.changingPassword = false;
      }
    });
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) this.closeChangePassword();
  }

  get roleLabel(): string {
    if (!this.user || this.user.roles.length === 0) return '';
    return this.user.roles[0].replace(/_/g, ' ').toLowerCase()
      .replace(/\b\w/g, c => c.toUpperCase());
  }
}
