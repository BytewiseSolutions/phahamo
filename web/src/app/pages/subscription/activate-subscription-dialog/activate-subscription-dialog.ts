import { Component, EventEmitter, Input, Output } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { SystemAdminSchool } from '../system-admin-school';
import { ActivateSubscriptionRequest } from '../activate-subscription-request';

@Component({
  selector: 'app-activate-subscription-dialog',
  standalone: false,
  templateUrl: './activate-subscription-dialog.html',
  styleUrl: './activate-subscription-dialog.scss'
})
export class ActivateSubscriptionDialog {

  @Input() visible = false;
  @Input() school: SystemAdminSchool | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  selectedPlan = 'MONTHLY';
  selectedMonths = 1;
  errorMessage = '';
  saving = false;

  planOptions = [
    { value: 'MONTHLY', label: 'Monthly' },
    { value: 'ANNUAL', label: 'Annual' },
    { value: 'ONE_OFF', label: 'One-Off (lifetime)' }
  ];

  monthOptions = [1, 3, 6, 12, 24];

  constructor(private backend: BackendService) {}

  get showMonths(): boolean {
    return this.selectedPlan !== 'ONE_OFF';
  }

  submit(): void {
    if (!this.school) return;

    this.saving = true;
    this.errorMessage = '';

    const body: ActivateSubscriptionRequest = {
      plan: this.selectedPlan,
      months: this.selectedPlan === 'ONE_OFF' ? 1 : this.selectedMonths
    };

    this.backend.post(
      `/admin/schools/${this.school.schoolId}/activate`, body
    ).subscribe({
      next: () => {
        this.saving = false;
        this.saved.emit();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to activate subscription.';
        this.saving = false;
      }
    });
  }

  close(): void {
    if (this.saving) return;
    this.errorMessage = '';
    this.selectedPlan = 'MONTHLY';
    this.selectedMonths = 1;
    this.closed.emit();
  }

  onOverlayClick(e: MouseEvent): void {
    if (e.target === e.currentTarget) this.close();
  }
}
