import { StudentFeePayment } from '../student-fee-payment';

export interface PaymentPage {
  content: StudentFeePayment[];
  number?: number;
  totalPages?: number;
  totalElements?: number;
  page?: {
    number: number;
    totalPages: number;
    totalElements: number;
  };
}
