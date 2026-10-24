import { Component, EventEmitter, Input, Output } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { SystemAdminSchool } from '../system-admin-school';
import { ExtendTrialRequest } from '../extend-trial-request';

@Component({
  selector: 'app-extend-trial-dialog',
  standalone: false,
  templateUrl: './extend-trial-dialog.html',
  styleUrl: './extend-trial-dialog.scss'
})
export class ExtendTrialDialog {

  @Input() visible = false;
  @Input() school: SystemAdminSchool | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  selectedDays = 30;
  customDays: number | null = null;
  errorMessage = '';
  saving = false;

  dayOptions = [7, 14, 30, 60, 90];

  constructor(private backend: BackendService) {}

  get effectiveDays(): number {
    return this.customDays ?? this.selectedDays;
  }

  chooseDays(days: number): void {
    this.selectedDays = days;
    this.customDays = null;
  }

  submit(): void {
    if (!this.school) return;

    const days = this.effectiveDays;
    if (!days || days < 1) {
      this.errorMessage = 'Please enter a valid number of days.';
      return;
    }

    this.saving = true;
    this.errorMessage = '';

    const body: ExtendTrialRequest = { days };

    this.backend.post(
      `/admin/schools/${this.school.schoolId}/extend-trial`, body
    ).subscribe({
      next: () => {
        this.saving = false;
        this.saved.emit();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to extend trial.';
        this.saving = false;
      }
    });
  }

  close(): void {
    if (this.saving) return;
    this.errorMessage = '';
    this.customDays = null;
    this.selectedDays = 30;
    this.closed.emit();
  }

  onOverlayClick(e: MouseEvent): void {
    if (e.target === e.currentTarget) this.close();
  }

  formatDate(value: string | null): string {
    if (!value) return '—';
    try {
      return new Date(value).toLocaleDateString('en-GB', {
        day: 'numeric', month: 'short', year: 'numeric', timeZone: 'UTC'
      });
    } catch {
      return value;
    }
  }
}
