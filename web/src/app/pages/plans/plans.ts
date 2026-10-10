import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../util/backend.service';
import { PlanPrice } from '../../util/plan-price';
import { Subscription } from '../../util/subscription';

@Component({
  selector: 'app-plans',
  standalone: false,
  templateUrl: './plans.html',
  styleUrl: './plans.scss'
})
export class Plans implements OnInit {

  prices: PlanPrice[] = [];
  subscription: Subscription | null = null;
  loading = false;
  errorMessage = '';

  showSubscribeModal = false;
  selectedPrice: PlanPrice | null = null;

  supportPhone = '+266 6235 4628';
  supportPhoneTel = '+26662354628';
  supportEmail = 'bytewisesolutions497@gmail.com';

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.loadPrices();
    this.loadSubscription();
  }

  loadPrices(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<PlanPrice[]>('/plan-prices/me').subscribe({
      next: (prices) => {
        this.prices = prices;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load plans.';
        this.loading = false;
      }
    });
  }

  loadSubscription(): void {
    this.backend.get<Subscription>('/subscriptions/me').subscribe({
      next: (sub) => this.subscription = sub,
      error: () => this.subscription = null
    });
  }

  onSubscribe(price: PlanPrice): void {
    this.selectedPrice = price;
    this.showSubscribeModal = true;
  }

  closeSubscribeModal(): void {
    this.showSubscribeModal = false;
    this.selectedPrice = null;
  }

  callSupport(): void {
    window.location.href = `tel:${this.supportPhoneTel}`;
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
}
