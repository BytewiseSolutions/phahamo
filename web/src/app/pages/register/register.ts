import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { BackendService } from '../../util/backend.service';
import { School, SchoolRegistration } from '../../util/school';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
  registerForm: FormGroup;
  currentStep = 1;
  submitted = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private backend: BackendService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      phone: ['', Validators.required],
      schoolName: ['', Validators.required],
      schoolAddress: ['', Validators.required],
      schoolContactEmail: ['', [Validators.required, Validators.email]],
      schoolContactPhone: ['', Validators.required]
    });
  }

  get step1Fields(): string[] {
    return ['fullName', 'email', 'password', 'phone'];
  }

  goToNextStep(): void {
    const step1Invalid = this.step1Fields.some(
      field => this.registerForm.get(field)?.invalid
    );

    if (step1Invalid) {
      this.step1Fields.forEach(field => this.registerForm.get(field)?.markAsTouched());
      return;
    }

    this.currentStep = 2;
  }

  goToPreviousStep(): void {
    this.currentStep = 1;
  }

  private splitFullName(fullName: string): { firstName: string; lastName: string } {
    const trimmed = fullName.trim();
    const firstSpaceIndex = trimmed.indexOf(' ');

    if (firstSpaceIndex === -1) {
      return { firstName: trimmed, lastName: '' };
    }

    return {
      firstName: trimmed.substring(0, firstSpaceIndex),
      lastName: trimmed.substring(firstSpaceIndex + 1).trim()
    };
  }

  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const formValue = this.registerForm.value;
    const { firstName, lastName } = this.splitFullName(formValue.fullName);

    const payload: SchoolRegistration = {
      firstName,
      lastName,
      email: formValue.email,
      password: formValue.password,
      phone: formValue.phone,
      school: {
        schoolName: formValue.schoolName,
        schoolAddress: formValue.schoolAddress,
        schoolContactEmail: formValue.schoolContactEmail,
        schoolContactPhone: formValue.schoolContactPhone
      }
    };

    this.backend.postSilently<School>('/schools/register', payload).subscribe({
      next: () => {
        this.successMessage = 'School registered successfully! Redirecting to login...';
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.errorMessage = err.status === 409
          ? (err.error?.message || 'That email or school is already registered.')
          : 'Something went wrong. Please try again.';
      }
    });
  }
}
