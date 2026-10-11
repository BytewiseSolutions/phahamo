import { Currency } from './school';
import { SubscriptionPlan } from './subscription-plan';
import { SubscriptionRequestStatus } from './subscription-request-status';

export interface SubscriptionRequest {
  id: number;
  schoolId: number;
  schoolName: string;
  plan: SubscriptionPlan;
  amount: number;
  currency: Currency;
  methodId: number | null;
  methodLabel: string | null;
  proofFileId: number | null;
  proofFileName: string | null;
  note: string | null;
  status: SubscriptionRequestStatus;
  reviewNote: string | null;
  createdAt: string;
}
