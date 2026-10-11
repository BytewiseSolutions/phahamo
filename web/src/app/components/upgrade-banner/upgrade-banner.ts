import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from '../../util/subscription';

@Component({
  selector: 'app-upgrade-banner',
  standalone: false,
  templateUrl: './upgrade-banner.html',
  styleUrl: './upgrade-banner.scss'
})
export class UpgradeBanner {

  @Input() subscription: Subscription | null = null;

  constructor(private router: Router) {}

  get shouldShow(): boolean {
    if (!this.subscription) return false;
    if (this.subscription.status === 'CANCELLED') return false;
    if (this.subscription.daysRemaining < 0) return false;
    if (this.subscription.status === 'EXPIRED') return true;
    return this.subscription.daysRemaining <= 25;
  }

  get isExpired(): boolean {
    return this.subscription?.status === 'EXPIRED';
  }

  get message(): string {
    if (!this.subscription) return '';

    if (this.isExpired) {
      return 'Your subscription has expired.';
    }

    const days = this.subscription.daysRemaining;
    if (days === 0) return 'Your subscription expires today.';
    if (days === 1) return 'Your subscription expires in 1 day.';
    return `Your subscription expires in ${days} days.`;
  }

  goToPlans(): void {
    this.router.navigate(['/plans']);
  }
}
