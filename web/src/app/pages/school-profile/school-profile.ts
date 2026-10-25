import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../util/auth.service';
import { BackendService } from '../../util/backend.service';
import { School } from '../../util/school';
import { Currency } from '../../util/currency';
import { TermStructure } from '../../util/term-structure';
import { CURRENCY_OPTIONS } from '../../util/currency-options';
import { TERM_STRUCTURE_OPTIONS } from '../../util/term-structure-options';
import { SchoolProfileUpdateRequest } from '../../util/school-profile-update-request';
import { Subscription } from '../../util/subscription';

@Component({
  selector: 'app-school-profile',
  standalone: false,
  templateUrl: './school-profile.html',
  styleUrl: './school-profile.scss'
})
export class SchoolProfile implements OnInit, OnDestroy {
  form: FormGroup;
  loading = true;
  saving = false;
  errorMessage = '';
  successMessage = '';

  logoPreviewUrl: string | null = null;
  selectedLogoFile: File | null = null;
  uploadingLogo = false;

  termStructureOptions = TERM_STRUCTURE_OPTIONS;
  currencyOptions = CURRENCY_OPTIONS;
  timezoneOptions: string[] = this.buildTimezoneOptions();

  private schoolId!: number;

  subscription: Subscription | null = null;
  subscriptionLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private backend: BackendService
  ) {
    this.form = this.fb.group({
      schoolName: ['', Validators.required],
      schoolAddress: ['', Validators.required],
      schoolContactEmail: ['', [Validators.required, Validators.email]],
      schoolContactPhone: ['', Validators.required],
      schoolTermStructure: [TermStructure.THREE_TERM, Validators.required],
      schoolCurrency: [Currency.LSL, Validators.required],
      schoolTimezone: ['Africa/Maseru', Validators.required],
      absenceAlertsEnabled: [true],
    });
  }

  ngOnInit(): void {
    const activeSchoolId = this.authService.getActiveSchoolId();
    if (!activeSchoolId) {
      this.errorMessage = 'No active school selected.';
      this.loading = false;
      return;
    }

    this.schoolId = Number(activeSchoolId);
    this.loadSchool();
    this.loadSubscription();
  }

  private loadSubscription(): void {
    this.subscriptionLoading = true;

    this.backend.get<Subscription>('/subscriptions/me').subscribe({
      next: (sub) => {
        this.subscription = sub;
        this.subscriptionLoading = false;
      },
      error: () => {
        this.subscription = null;
        this.subscriptionLoading = false;
      }
    });
  }

  formatDate(value: string | null): string {
    if (!value) return '—';
    try {
      return new Date(value).toLocaleDateString('en-GB', {
        day: 'numeric',
        month: 'long',
        year: 'numeric',
        timeZone: 'UTC'
      });
    } catch {
      return value;
    }
  }


  formatEnum(value: string): string {
    if (!value) return '—';
    return value.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }

  get subscriptionTone(): 'ok' | 'warn' | 'danger' {
    if (!this.subscription) return 'ok';
    if (this.subscription.status === 'EXPIRED') return 'danger';
    if (this.subscription.daysRemaining < 0) return 'ok';
    if (this.subscription.daysRemaining <= 7) return 'danger';
    if (this.subscription.daysRemaining <= 30) return 'warn';
    return 'ok';
  }

  get daysRemainingLabel(): string {
    if (!this.subscription) return '—';
    if (this.subscription.daysRemaining < 0) return 'Lifetime access';
    if (this.subscription.status === 'EXPIRED') return 'Expired';
    if (this.subscription.daysRemaining === 0) return 'Expires today';
    if (this.subscription.daysRemaining === 1) return '1 day';
    return `${this.subscription.daysRemaining} days`;
  }

  ngOnDestroy(): void {
    if (this.logoPreviewUrl) {
      URL.revokeObjectURL(this.logoPreviewUrl);
    }
  }

  private loadSchool(): void {
    this.loading = true;
    this.backend.get<School>(`/schools/${this.schoolId}`).subscribe({
      next: (school) => {
        this.patchForm(school);
        this.loading = false;
        if (school.schoolLogoId) {
          this.loadLogoPreview();
        }
      },
      error: () => {
        this.errorMessage = 'Could not load school profile.';
        this.loading = false;
      }
    });
  }

  private patchForm(school: School): void {
    this.form.patchValue({
      schoolName: school.schoolName,
      schoolAddress: school.schoolAddress,
      schoolContactEmail: school.schoolContactEmail,
      schoolContactPhone: school.schoolContactPhone,
      schoolTermStructure: school.schoolTermStructure,
      schoolCurrency: school.schoolCurrency,
      schoolTimezone: school.schoolTimezone,
      absenceAlertsEnabled: school.absenceAlertsEnabled ?? true,
    });
  }

  private loadLogoPreview(): void {
    this.backend.getBlob(`/schools/${this.schoolId}/logo`).subscribe({
      next: (blob) => {
        if (this.logoPreviewUrl) {
          URL.revokeObjectURL(this.logoPreviewUrl);
        }
        this.logoPreviewUrl = URL.createObjectURL(blob);
      },
      error: () => {
        // No logo yet, or failed to load — not fatal, just skip the preview.
      }
    });
  }

  onLogoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedLogoFile = input.files?.[0] ?? null;
  }

  uploadLogo(): void {
    if (!this.selectedLogoFile) return;

    const formData = new FormData();
    formData.append('file', this.selectedLogoFile);

    this.uploadingLogo = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.backend.post<School>(`/schools/${this.schoolId}/logo`, formData).subscribe({
      next: () => {
        this.uploadingLogo = false;
        this.selectedLogoFile = null;
        this.successMessage = 'Logo updated.';
        this.loadLogoPreview();
      },
      error: () => {
        this.uploadingLogo = false;
        this.errorMessage = 'Could not upload logo.';
      }
    });
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    const payload: SchoolProfileUpdateRequest = this.form.value;

    this.backend.put<School>(`/schools/${this.schoolId}`, payload).subscribe({
      next: () => {
        this.saving = false;
        this.successMessage = 'School profile updated.';
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message ?? 'Could not update school profile.';
      }
    });
  }

  private buildTimezoneOptions(): string[] {
    const fallback = ['Africa/Maseru', 'Africa/Johannesburg', 'UTC'];
    const intlWithZones = Intl as typeof Intl & { supportedValuesOf?: (key: string) => string[] };
    if (typeof intlWithZones.supportedValuesOf === 'function') {
      try {
        return intlWithZones.supportedValuesOf('timeZone');
      } catch {
        return fallback;
      }
    }
    return fallback;
  }
}
