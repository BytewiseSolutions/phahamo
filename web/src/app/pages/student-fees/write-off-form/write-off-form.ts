import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { StudentFeeAccount } from '../student-fee-account';

@Component({
  selector: 'app-write-off-form',
  standalone: false,
  templateUrl: './write-off-form.html',
  styleUrls: ['./write-off-form.scss']
})
export class WriteOffForm implements OnChanges {
  @Input() visible = false;
  @Input() accountId: number | null = null;
  @Input() termLabel = '';
  @Input() maxAmount = 0;
  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  amount: number | null = null;
  reason = '';
  saving = false;
  errorMessage = '';

  constructor(private backend: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible']?.currentValue === true) {
      this.amount = this.maxAmount;
      this.reason = '';
      this.errorMessage = '';
      this.saving = false;
    }
  }

  onSave(): void {
    this.errorMessage = '';
    if (!this.accountId) {
      this.errorMessage = 'No previous account selected.';
      return;
    }
    if (!this.amount || this.amount <= 0 || this.amount > this.maxAmount) {
      this.errorMessage = `Amount must be between M 0.01 and M ${this.maxAmount.toFixed(2)}.`;
      return;
    }
    if (!this.reason.trim()) {
      this.errorMessage = 'A reason is required.';
      return;
    }

    this.saving = true;
    this.backend.postSilently<StudentFeeAccount>(
      `/student-fees/${this.accountId}/write-off`,
      { amount: this.amount, reason: this.reason.trim() }
    ).subscribe({
      next: () => {
        this.saving = false;
        this.saved.emit();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to write off balance.';
        this.saving = false;
      }
    });
  }

  onClose(): void {
    if (!this.saving) this.closed.emit();
  }
}
