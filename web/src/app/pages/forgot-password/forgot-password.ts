import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BackendService } from '../../util/backend.service';
import { ForgotPasswordRequest } from '../../util/auth';

@Component({
  selector: 'app-forgot-password',
  standalone: false,
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.scss'
})
export class ForgotPassword {
  forgotForm: FormGroup;
  submitted = false;
  errorMessage = '';
  successMessage = '';

  constructor(private fb: FormBuilder, private backend: BackendService) {
    this.forgotForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.forgotForm.invalid) {
      this.forgotForm.markAllAsTouched();
      return;
    }

    const payload: ForgotPasswordRequest = this.forgotForm.value;

    this.backend.post<void>('/auth/forgot-password', payload).subscribe({
      next: () => {
        this.successMessage = 'If that email is registered, a reset link has been sent.';
      },
      error: () => {
        // Same message on error too — never reveal whether the email exists.
        this.successMessage = 'If that email is registered, a reset link has been sent.';
      }
    });
  }
}
