import { Currency } from './currency';
import { SubscriptionPlan } from './subscription-plan';

export interface PlanPrice {
  id: number;
  plan: SubscriptionPlan;
  amount: number;
  currency: Currency;
  active: boolean;
}
