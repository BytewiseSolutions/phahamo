import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { PlanPrice } from '../../../util/plan-price';
import { SubscriptionPlan } from '../../../util/subscription-plan';
import { Currency } from '../../../util/currency';

@Component({
  selector: 'app-plan-price-list',
  standalone: false,
  templateUrl: './plan-price-list.html',
  styleUrl: './plan-price-list.scss'
})
export class PlanPriceList implements OnInit {

  prices: PlanPrice[] = [];
  loading = false;
  errorMessage = '';

  showForm = false;
  editingPrice: PlanPrice | null = null;
  saving = false;

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.loadPrices();
  }

  loadPrices(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<PlanPrice[]>('/plan-prices').subscribe({
      next: (prices) => {
        this.prices = prices;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load prices.';
        this.loading = false;
      }
    });
  }

  openCreate(): void {
    this.editingPrice = null;
    this.showForm = true;
    this.errorMessage = '';
  }

  openEdit(price: PlanPrice): void {
    this.editingPrice = price;
    this.showForm = true;
    this.errorMessage = '';
  }

  onFormClosed(): void {
    this.showForm = false;
    this.editingPrice = null;
    this.saving = false;
  }

  onSave(event: {
    id: number | null;
    plan: SubscriptionPlan | null;
    currency: Currency | null;
    amount: number;
    active: boolean;
  }): void {
    this.saving = true;
    this.errorMessage = '';

    if (event.id !== null) {
      const payload = {
        amount: event.amount,
        active: event.active
      };

      this.backend.put<PlanPrice>(`/plan-prices/${event.id}`, payload).subscribe({
        next: () => {
          this.saving = false;
          this.onFormClosed();
          this.loadPrices();
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = err.error?.message || 'Failed to update price.';
        }
      });
    } else {
      const payload = {
        plan: event.plan,
        currency: event.currency,
        amount: event.amount,
        active: event.active
      };

      this.backend.post<PlanPrice>('/plan-prices', payload).subscribe({
        next: () => {
          this.saving = false;
          this.onFormClosed();
          this.loadPrices();
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = err.error?.message || 'Failed to create price.';
        }
      });
    }
  }

  formatEnum(value: string): string {
    if (!value) return '—';
    return value.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }

  formatAmount(price: PlanPrice): string {
    return `${price.currency} ${Number(price.amount).toFixed(2)}`;
  }
}
