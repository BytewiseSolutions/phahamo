import { SubscriptionPlan } from './subscription-plan';
import { SubscriptionStatus } from './subscription-status';

export interface Subscription {
  id: number;
  schoolId: number;
  schoolName: string;
  plan: SubscriptionPlan;
  status: SubscriptionStatus;
  startedAt: string;
  endsAt: string | null;
  daysRemaining: number;
  months?: number;
}
