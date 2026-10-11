import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../util/backend.service';
import { PlanPrice } from '../../util/plan-price';
import { Subscription } from '../../util/subscription';
import {SubscriptionRequest} from '../../util/subscription-request';

@Component({
  selector: 'app-plans',
  standalone: false,
  templateUrl: './plans.html',
  styleUrl: './plans.scss'
})
export class Plans implements OnInit {

  prices: PlanPrice[] = [];
  subscription: Subscription | null = null;
  pendingRequest: SubscriptionRequest | null = null;

  loading = false;
  errorMessage = '';

  showSubscribeModal = false;
  selectedPrice: PlanPrice | null = null;
  submitting = false;

  supportPhone = '+266 6235 4628';
  supportPhoneTel = '+26662354628';
  supportEmail = 'bytewisesolutions497@gmail.com';

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.loadPrices();
    this.loadSubscription();
    this.loadRequests();
  }

  loadPrices(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<PlanPrice[]>('/plan-prices/me').subscribe({
      next: (prices) => {
        this.prices = prices.filter(p => p.plan !== 'FREE_TRIAL');
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

  loadRequests(): void {
    this.backend.get<SubscriptionRequest[]>('/subscription-requests/my-school').subscribe({
      next: (reqs) => {
        this.pendingRequest = reqs.find(r => r.status === 'PENDING') ?? null;
      },
      error: () => {
        this.pendingRequest = null;
      }
    });
  }

  onSubscribe(price: PlanPrice): void {
    if (this.pendingRequest) {
      this.errorMessage = 'You already have a pending subscription request.';
      return;
    }

    this.selectedPrice = price;
    this.showSubscribeModal = true;
    this.errorMessage = '';
  }

  onSubscribeClosed(): void {
    this.showSubscribeModal = false;
    this.selectedPrice = null;
    this.submitting = false;
  }

  onSubscribeSubmitted(event: {
    plan: string;
    amount: number;
    currency: string;
    methodId: number;
    proofFileId: number;
    note: string | null;
  }): void {
    this.submitting = true;
    this.errorMessage = '';

    this.backend.postSilently<SubscriptionRequest>('/subscription-requests', event).subscribe({
      next: () => {
        this.submitting = false;
        this.onSubscribeClosed();
        this.loadRequests();
      },
      error: (err) => {
        this.submitting = false;
        this.errorMessage = err.error?.message || 'Failed to submit request.';
      }
    });
  }

  cancelPendingRequest(): void {
    if (!this.pendingRequest) return;

    this.backend.delete<SubscriptionRequest>(`/subscription-requests/${this.pendingRequest.id}`).subscribe({
      next: () => this.loadRequests(),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to cancel request.';
      }
    });
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

  formatDate(value: string): string {
    if (!value) return '—';
    try {
      return new Date(value).toLocaleDateString('en-GB', {
        day: 'numeric',
        month: 'short',
        year: 'numeric',
        timeZone: 'UTC'
      });
    } catch {
      return value;
    }
  }
}
