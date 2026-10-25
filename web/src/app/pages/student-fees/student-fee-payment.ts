import {PaymentMethodType} from '../../util/payment-method';

export interface StudentFeePayment {
  id: number;
  amount: number;
  method: PaymentMethodType;
  reference?: string;
  paidAt: string;
  balanceAfter: number;
  receivedById?: number;
  receivedByName?: string;
  studentName?: string;
  status: 'ACTIVE' | 'REVERSED';
  reversalReason?: string;
  reversedAt?: string;
  reversedByName?: string;
}

export type StudentFeePaymentInput = Pick<
  StudentFeePayment,
  'amount' | 'method' | 'reference' | 'paidAt'
>;
