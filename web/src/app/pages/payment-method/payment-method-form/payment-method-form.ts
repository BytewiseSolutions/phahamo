import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { PaymentMethod, PaymentMethodType } from '../../../util/payment-method';

@Component({
  selector: 'app-payment-method-form',
  standalone: false,
  templateUrl: './payment-method-form.html',
  styleUrl: './payment-method-form.scss'
})
export class PaymentMethodForm implements OnChanges {

  @Input() visible = false;
  @Input() editingMethod: PaymentMethod | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() save = new EventEmitter<{
    id: number | null;
    type: PaymentMethodType;
    label: string;
    details: string;
    active: boolean;
    sortOrder: number;
  }>();

  types = [PaymentMethodType.MPESA, PaymentMethodType.ECOCASH, PaymentMethodType.EFT, PaymentMethodType.CARD];

  formType: PaymentMethodType = PaymentMethodType.MPESA;
  formLabel = '';
  formDetails = '';
  formActive = true;
  formSortOrder = 0;

  get isEditMode(): boolean {
    return this.editingMethod !== null;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible) {
      if (this.editingMethod) {
        this.formType = this.editingMethod.type;
        this.formLabel = this.editingMethod.label;
        this.formDetails = this.editingMethod.details;
        this.formActive = this.editingMethod.active;
        this.formSortOrder = this.editingMethod.sortOrder;
      } else {
        this.formType = PaymentMethodType.MPESA;
        this.formLabel = '';
        this.formDetails = '';
        this.formActive = true;
        this.formSortOrder = 0;
      }
    }
  }

  onSubmit(): void {
    if (!this.formLabel.trim()) return;

    this.save.emit({
      id: this.editingMethod?.id ?? null,
      type: this.formType,
      label: this.formLabel.trim(),
      details: this.formDetails,
      active: this.formActive,
      sortOrder: this.formSortOrder
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

  formatEnum(value: string): string {
    if (!value) return '—';
    return value.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }
}
