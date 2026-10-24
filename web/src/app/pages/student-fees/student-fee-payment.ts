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
}

export type StudentFeePaymentInput = Pick<
  StudentFeePayment,
  'amount' | 'method' | 'reference' | 'paidAt'
>;
