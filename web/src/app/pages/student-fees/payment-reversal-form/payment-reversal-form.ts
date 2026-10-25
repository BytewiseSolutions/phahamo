import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { StudentFeeAccount } from '../student-fee-account';
import { StudentFeePayment } from '../student-fee-payment';

@Component({
  selector: 'app-payment-reversal-form',
  standalone: false,
  templateUrl: './payment-reversal-form.html',
  styleUrls: ['./payment-reversal-form.scss']
})
export class PaymentReversalForm implements OnChanges {
  @Input() visible = false;
  @Input() payment: StudentFeePayment | null = null;
  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  reason = '';
  saving = false;
  errorMessage = '';

  constructor(private backend: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible']?.currentValue === true) {
      this.reason = '';
      this.errorMessage = '';
      this.saving = false;
    }
  }

  onSubmit(): void {
    this.errorMessage = '';
    if (!this.payment) {
      this.errorMessage = 'No payment selected.';
      return;
    }
    if (!this.reason.trim()) {
      this.errorMessage = 'A reason is required.';
      return;
    }

    this.saving = true;
    this.backend.postSilently<StudentFeeAccount>(
      `/student-fees/payments/${this.payment.id}/reverse`,
      { reason: this.reason.trim() }
    ).subscribe({
      next: () => {
        this.saving = false;
        this.saved.emit();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to reverse payment.';
        this.saving = false;
      }
    });
  }

  onClose(): void {
    if (!this.saving) this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) this.onClose();
  }
}
