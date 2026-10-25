import { ParentPayment } from './parent-payment';

export interface ParentFeeAccount {
  totalFees: number;
  totalAdjustments: number;
  amountPaid: number;
  balance: number;
  payments: ParentPayment[];
}
