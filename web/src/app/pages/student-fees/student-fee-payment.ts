import {PaymentMethodType} from '../../util/payment-method';

export interface StudentFeePayment {
  id: number;
  amount: number;
  method: PaymentMethodType;
  reference?: string;
  paidAt: string;
  receivedById?: number;
  receivedByName?: string;
}
