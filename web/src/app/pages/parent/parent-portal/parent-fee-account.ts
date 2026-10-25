import { ParentPayment } from './parent-payment';
import { ParentPreviousBalance } from './parent-previous-balance';

export interface ParentFeeAccount {
  studentFirstName: string;
  studentLastName: string;
  admissionNumber: string;
  termName: string;
  academicYearName: string;
  className: string;
  totalFees: number;
  totalAdjustments: number;
  amountPaid: number;
  balance: number;
  currentTermBalance: number;
  previousBalance: number;
  payments: ParentPayment[];
  previousBalances: ParentPreviousBalance[];
}
