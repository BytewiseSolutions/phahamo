import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Subscription } from '../../../util/subscription';
import { SubscriptionPlan } from '../../../util/subscription-plan';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';

@Component({
  selector: 'app-subscription-list',
  standalone: false,
  templateUrl: './subscription-list.html',
  styleUrl: './subscription-list.scss'
})
export class SubscriptionList implements OnInit {

  subscriptions: Subscription[] = [];
  loading = false;
  errorMessage = '';

  plans = [SubscriptionPlan.MONTHLY, SubscriptionPlan.ANNUAL, SubscriptionPlan.ONE_OFF];

  selectedSubscription: Subscription | null = null;
  saving = false;

  showExtendForm = false;
  extendMonths = 1;

  showChangePlanForm = false;
  selectedPlan: SubscriptionPlan = SubscriptionPlan.MONTHLY;
  selectedMonths = 1;

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService
  ) {}

  ngOnInit(): void {
    this.loadSubscriptions();
  }

  loadSubscriptions(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<Subscription[]>('/subscriptions').subscribe({
      next: (subs) => {
        this.subscriptions = subs;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load subscriptions.';
        this.loading = false;
      }
    });
  }

  openExtend(subscription: Subscription): void {
    this.selectedSubscription = subscription;
    this.extendMonths = 1;
    this.showExtendForm = true;
    this.errorMessage = '';
  }

  closeExtend(): void {
    this.showExtendForm = false;
    this.selectedSubscription = null;
    this.saving = false;
  }

  submitExtend(): void {
    if (!this.selectedSubscription) return;

    if (this.extendMonths < 1) {
      this.errorMessage = 'Months must be at least 1.';
      return;
    }

    this.saving = true;
    this.errorMessage = '';

    this.backend.post<Subscription>(
      `/subscriptions/school/${this.selectedSubscription.schoolId}/extend`,
      { months: this.extendMonths }
    ).subscribe({
      next: () => {
        this.saving = false;
        this.closeExtend();
        this.loadSubscriptions();
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'Failed to extend subscription.';
      }
    });
  }

  get projectedExtendDate(): string {
    if (!this.selectedSubscription) return '—';

    const now = new Date();
    const current = this.selectedSubscription.endsAt
      ? new Date(this.selectedSubscription.endsAt)
      : null;

    const base = current && current > now ? current : now;
    const projected = new Date(base.getTime() + this.extendMonths * 30 * 24 * 60 * 60 * 1000);

    return projected.toLocaleDateString('en-GB', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
      timeZone: 'UTC'
    });
  }

  openChangePlan(subscription: Subscription): void {
    this.selectedSubscription = subscription;
    this.selectedPlan = subscription.plan;
    this.selectedMonths = 1;
    this.showChangePlanForm = true;
    this.errorMessage = '';
  }

  closeChangePlan(): void {
    this.showChangePlanForm = false;
    this.selectedSubscription = null;
    this.saving = false;
  }

  onPlanChange(plan: SubscriptionPlan): void {
    this.selectedPlan = plan;
  }

  submitChangePlan(): void {
    if (!this.selectedSubscription) return;

    this.saving = true;
    this.errorMessage = '';

    const payload = {
      plan: this.selectedPlan,
      months: this.selectedPlan === SubscriptionPlan.ONE_OFF ? 0 : this.selectedMonths
    };

    this.backend.post<Subscription>(
      `/subscriptions/school/${this.selectedSubscription.schoolId}/change-plan`,
      payload
    ).subscribe({
      next: () => {
        this.saving = false;
        this.closeChangePlan();
        this.loadSubscriptions();
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'Failed to change plan.';
      }
    });
  }

  async onCancel(subscription: Subscription): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Cancel Subscription',
      message: `Cancel the subscription for "${subscription.schoolName}"?`,
      confirmText: 'Cancel Subscription',
      danger: true
    });

    if (!confirmed) return;

    this.errorMessage = '';

    this.backend.post<Subscription>(
      `/subscriptions/school/${subscription.schoolId}/cancel`,
      {}
    ).subscribe({
      next: () => this.loadSubscriptions(),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to cancel subscription.';
      }
    });
  }

  formatDate(value: string | null): string {
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

  formatEnum(value: string): string {
    if (!value) return '—';
    return value.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }

  daysRemainingLabel(sub: Subscription): string {
    if (sub.daysRemaining < 0) return 'Lifetime';
    if (sub.status === 'EXPIRED') return 'Expired';
    if (sub.daysRemaining === 0) return 'Today';
    if (sub.daysRemaining === 1) return '1 day';
    return `${sub.daysRemaining} days`;
  }

  daysRemainingTone(sub: Subscription): 'ok' | 'warn' | 'danger' {
    if (sub.status === 'EXPIRED' || sub.status === 'CANCELLED') return 'danger';
    if (sub.daysRemaining < 0) return 'ok';
    if (sub.daysRemaining <= 7) return 'danger';
    if (sub.daysRemaining <= 30) return 'warn';
    return 'ok';
  }
}
