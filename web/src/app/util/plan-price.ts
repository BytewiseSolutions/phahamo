import { Currency } from './school';
import { SubscriptionPlan } from './subscription-plan';

export interface PlanPrice {
  id: number;
  plan: SubscriptionPlan;
  amount: number;
  currency: Currency;
  active: boolean;
}
