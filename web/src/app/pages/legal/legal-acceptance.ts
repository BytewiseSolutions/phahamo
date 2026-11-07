import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { BackendService } from '../../util/backend.service';
import { AuthService } from '../../util/auth.service';

@Component({
  selector: 'app-legal-acceptance',
  standalone: false,
  templateUrl: './legal-acceptance.html',
  styleUrl: './legal-acceptance.scss'
})
export class LegalAcceptance {
  form: FormGroup;
  errorMessage = '';
  saving = false;

  constructor(
    private fb: FormBuilder,
    private backend: BackendService,
    private auth: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      termsAccepted: [false, Validators.requiredTrue],
      privacyAccepted: [false, Validators.requiredTrue]
    });
  }

  submit(): void {
    this.errorMessage = '';
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving = true;
    this.backend.postSilently<void>('/auth/legal-acceptance', this.form.getRawValue()).subscribe({
      next: () => {
        this.saving = false;
        this.auth.clearLegalAcceptanceRequired();
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.saving = false;
        this.errorMessage = 'We could not save your acceptance. Please try again.';
      }
    });
  }
}
