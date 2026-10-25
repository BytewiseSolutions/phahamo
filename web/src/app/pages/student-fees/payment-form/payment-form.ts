import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { StudentFeePayment, StudentFeePaymentInput } from '../student-fee-payment';
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
  duplicatePayment: StudentFeePayment | null = null;

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
    this.duplicatePayment = null;
    this.saving = false;
  }

  onSave(): void {
    if (this.saving) return;
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

    this.recordPayment(false);
  }

  confirmDuplicatePayment(): void {
    if (!this.duplicatePayment) return;
    this.duplicatePayment = null;
    this.recordPayment(true);
  }

  private recordPayment(confirmDuplicate: boolean): void {
    const payload: StudentFeePaymentInput = {
      amount: this.amount!,
      method: this.method,
      reference: this.reference?.trim() || undefined,
      paidAt: this.paidAt + 'T12:00:00Z',
      confirmDuplicate
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
        if (err.status === 409 && err.error?.code === 'DUPLICATE_PAYMENT') {
          this.duplicatePayment = err.error.existingPayment;
          this.errorMessage = '';
        } else {
          this.errorMessage = err.error?.message || 'Failed to record payment.';
        }
        this.saving = false;
      }
    });
  }

  cancelDuplicate(): void {
    this.duplicatePayment = null;
  }

  formatDate(value: string | undefined): string {
    if (!value) return '—';
    return new Date(value).toLocaleDateString('en-GB', {
      day: 'numeric', month: 'short', year: 'numeric'
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
