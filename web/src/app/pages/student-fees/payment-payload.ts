import {PaymentMethodType} from '../../util/payment-method';

export interface PaymentPayload {
  amount: number;
  method: PaymentMethodType;
  reference?: string;
  paidAt?: string;
}
