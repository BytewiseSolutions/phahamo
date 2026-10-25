import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { PlanPrice } from '../../../util/plan-price';
import { SubscriptionPlan } from '../../../util/subscription-plan';
import { Currency } from '../../../util/currency';

@Component({
  selector: 'app-plan-price-form',
  standalone: false,
  templateUrl: './plan-price-form.html',
  styleUrl: './plan-price-form.scss'
})
export class PlanPriceForm implements OnChanges {

  @Input() visible = false;
  @Input() editingPrice: PlanPrice | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() save = new EventEmitter<{
    id: number | null;
    plan: SubscriptionPlan | null;
    currency: Currency | null;
    amount: number;
    active: boolean;
  }>();

  plans = [SubscriptionPlan.MONTHLY, SubscriptionPlan.ANNUAL, SubscriptionPlan.ONE_OFF];
  currencies = [Currency.LSL, Currency.ZAR];

  formPlan: SubscriptionPlan = SubscriptionPlan.MONTHLY;
  formCurrency: Currency = Currency.LSL;
  formAmount = 0;
  formActive = true;

  get isEditMode(): boolean {
    return this.editingPrice !== null;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible) {
      if (this.editingPrice) {
        this.formPlan = this.editingPrice.plan;
        this.formCurrency = this.editingPrice.currency;
        this.formAmount = Number(this.editingPrice.amount);
        this.formActive = this.editingPrice.active;
      } else {
        this.formPlan = SubscriptionPlan.MONTHLY;
        this.formCurrency = Currency.LSL;
        this.formAmount = 0;
        this.formActive = true;
      }
    }
  }

  onSubmit(): void {
    if (this.formAmount < 0) return;

    this.save.emit({
      id: this.editingPrice?.id ?? null,
      plan: this.isEditMode ? null : this.formPlan,
      currency: this.isEditMode ? null : this.formCurrency,
      amount: this.formAmount,
      active: this.formActive
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
