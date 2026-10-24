import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { AdjustmentType } from '../adjustment-type';
import { StudentFeeAdjustmentInput } from '../student-fee-adjustment';
import { StudentFeeAccount } from '../student-fee-account';

@Component({
  selector: 'app-adjustment-form',
  standalone: false,
  templateUrl: './adjustment-form.html',
  styleUrl: './adjustment-form.scss'
})
export class AdjustmentForm implements OnChanges {

  @Input() visible = false;
  @Input() accountId: number | null = null;
  @Input() maxAmount = 0;

  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  type: AdjustmentType = AdjustmentType.DISCOUNT;
  amount: number | null = null;
  reason = '';

  saving = false;
  errorMessage = '';

  types: AdjustmentType[] = [
    AdjustmentType.DISCOUNT,
    AdjustmentType.SCHOLARSHIP,
    AdjustmentType.WAIVER
  ];

  constructor(private backend: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible']?.currentValue === true) {
      this.reset();
    }
  }

  private reset(): void {
    this.type = AdjustmentType.DISCOUNT;
    this.amount = null;
    this.reason = '';
    this.errorMessage = '';
    this.saving = false;
  }

  typeLabel(type: AdjustmentType): string {
    return type.charAt(0) + type.slice(1).toLowerCase();
  }

  onSave(): void {
    this.errorMessage = '';

    if (!this.accountId) {
      this.errorMessage = 'No account selected.';
      return;
    }
    if (!this.amount || this.amount <= 0) {
      this.errorMessage = 'Amount must be greater than 0.';
      return;
    }
    if (this.amount > this.maxAmount) {
      this.errorMessage = `Amount cannot exceed remaining balance (M ${this.maxAmount.toFixed(2)}).`;
      return;
    }
    if (!this.reason || this.reason.trim() === '') {
      this.errorMessage = 'Reason is required.';
      return;
    }

    const payload: StudentFeeAdjustmentInput = {
      type: this.type,
      amount: this.amount,
      reason: this.reason.trim()
    };

    this.saving = true;

    this.backend.postSilently<StudentFeeAccount>(
      `/student-fees/${this.accountId}/adjustments`, payload
    ).subscribe({
      next: () => {
        this.saving = false;
        this.saved.emit();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to save adjustment.';
        this.saving = false;
      }
    });
  }

  onClose(): void {
    if (this.saving) return;
    this.closed.emit();
  }
}
