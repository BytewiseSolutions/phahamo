import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { PaymentPayload } from '../payment-payload';
import { StudentFeeAccount } from '../student-fee-account';
import { PaymentMethodType } from '../../../util/payment-method';

@Component({
  selector: 'app-payment-form',
  standalone: false,
  templateUrl: './payment-form.html',
  styleUrl: './payment-form.scss'
})
export class PaymentForm implements OnChanges {

  @Input() visible = false;
  @Input() accountId: number | null = null;
  @Input() maxAmount = 0;

  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  method: PaymentMethodType = PaymentMethodType.CASH;
  amount: number | null = null;
  paidAt = '';
  reference = '';

  saving = false;
  errorMessage = '';

  methods: { value: PaymentMethodType; label: string }[] = [
    { value: PaymentMethodType.CASH, label: 'Cash' },
    { value: PaymentMethodType.MPESA, label: 'M-Pesa' },
    { value: PaymentMethodType.ECOCASH, label: 'EcoCash' },
    { value: PaymentMethodType.EFT, label: 'Bank Transfer (EFT)' },
    { value: PaymentMethodType.CARD, label: 'Card' }
  ];

  constructor(private backend: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible']?.currentValue === true) {
      this.reset();
    }
  }

  private reset(): void {
    this.method = PaymentMethodType.CASH;
    this.amount = null;
    this.paidAt = this.iso(new Date());
    this.reference = '';
    this.errorMessage = '';
    this.saving = false;
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
    if (!this.paidAt) {
      this.errorMessage = 'Payment date is required.';
      return;
    }

    const payload: PaymentPayload = {
      amount: this.amount,
      method: this.method,
      reference: this.reference?.trim() || undefined,
      paidAt: this.paidAt + 'T12:00:00Z'
    };

    this.saving = true;

    this.backend.postSilently<StudentFeeAccount>(
      `/student-fees/${this.accountId}/payments`, payload
    ).subscribe({
      next: () => {
        this.saving = false;
        this.saved.emit();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to record payment.';
        this.saving = false;
      }
    });
  }

  onClose(): void {
    if (this.saving) return;
    this.closed.emit();
  }

  private iso(d: Date): string {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
