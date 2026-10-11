import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { PlanPrice } from '../../../util/plan-price';
import { PaymentMethod } from '../../../util/payment-method';
import { FileInfo } from '../../../util/file';

@Component({
  selector: 'app-subscribe-form',
  standalone: false,
  templateUrl: './subscribe-form.html',
  styleUrl: './subscribe-form.scss'
})
export class SubscribeForm implements OnChanges {

  @Input() visible = false;
  @Input() price: PlanPrice | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() submitted = new EventEmitter<{
    plan: string;
    amount: number;
    currency: string;
    methodId: number;
    proofFileId: number;
    note: string | null;
  }>();

  paymentMethods: PaymentMethod[] = [];
  loadingMethods = false;

  selectedMethodId: number | null = null;
  selectedProofFileId: number | null = null;
  selectedProofFileName = '';
  uploadingProof = false;
  note = '';
  errorMessage = '';

  constructor(private backend: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible) {
      this.reset();
      this.loadPaymentMethods();
    }
  }

  loadPaymentMethods(): void {
    this.loadingMethods = true;

    this.backend.get<PaymentMethod[]>('/payment-methods/active').subscribe({
      next: (methods) => {
        this.paymentMethods = methods;
        this.selectedMethodId = methods.length > 0 ? methods[0].id : null;
        this.loadingMethods = false;
      },
      error: () => {
        this.paymentMethods = [];
        this.loadingMethods = false;
      }
    });
  }

  get selectedMethod(): PaymentMethod | null {
    if (!this.selectedMethodId) return null;
    return this.paymentMethods.find(m => m.id === this.selectedMethodId) ?? null;
  }

  onProofSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    this.uploadingProof = true;
    this.errorMessage = '';

    this.backend.postSilently<FileInfo>('/files', formData).subscribe({
      next: (info) => {
        this.selectedProofFileId = info.id;
        this.selectedProofFileName = info.name;
        this.uploadingProof = false;
      },
      error: (err) => {
        this.uploadingProof = false;
        this.errorMessage = err.error?.message || 'Failed to upload proof.';
      }
    });
  }

  onSubmit(): void {
    if (!this.price) return;

    if (!this.selectedMethodId) {
      this.errorMessage = 'Please select a payment method.';
      return;
    }

    if (!this.selectedProofFileId) {
      this.errorMessage = 'Please upload proof of payment.';
      return;
    }

    this.submitted.emit({
      plan: this.price.plan,
      amount: this.price.amount,
      currency: this.price.currency,
      methodId: this.selectedMethodId,
      proofFileId: this.selectedProofFileId,
      note: this.note || null
    });
  }

  onClose(): void {
    this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }

  formatPlanLabel(plan: string): string {
    switch (plan) {
      case 'MONTHLY':  return 'Monthly';
      case 'ANNUAL':   return 'Annual';
      case 'ONE_OFF':  return 'Lifetime';
      default:         return plan;
    }
  }

  formatPrice(price: PlanPrice): string {
    return `${price.currency} ${Number(price.amount).toFixed(2)}`;
  }

  getPlanSuffix(plan: string): string {
    switch (plan) {
      case 'MONTHLY':  return '/ month';
      case 'ANNUAL':   return '/ year';
      case 'ONE_OFF':  return ' once';
      default:         return '';
    }
  }

  private reset(): void {
    this.selectedMethodId = null;
    this.selectedProofFileId = null;
    this.selectedProofFileName = '';
    this.uploadingProof = false;
    this.note = '';
    this.errorMessage = '';
  }
}
