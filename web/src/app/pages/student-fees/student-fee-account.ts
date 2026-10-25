import { StudentFeeLine } from './student-fee-line';
import { StudentFeeAdjustment } from './student-fee-adjustment';
import { StudentFeePayment } from './student-fee-payment';

export interface StudentFeeAccount {
  id: number;
  studentId: number;
  studentFirstName: string;
  studentLastName: string;
  admissionNumber: string;
  termId: number;
  termName: string;
  academicYearName: string;
  classId: number;
  className: string;
  totalFees: number;
  totalAdjustments: number;
  amountPaid: number;
  balance: number;
  currentTermBalance: number;
  previousBalance: number;
  previousBalances: PreviousBalance[];
  lines: StudentFeeLine[];
  adjustments: StudentFeeAdjustment[];
  payments: StudentFeePayment[];
  lastReminderSentAt: string | null;
  lastReminderAmount: number | null;
  hasEmailParent: boolean;
  parents: { fullName: string; email: string | null; phone: string | null; hasEmail: boolean }[];
  lastStatementSentAt: string | null;
}

export interface PreviousBalance {
  accountId: number;
  termId: number;
  termName: string;
  academicYearName?: string;
  amount: number;
}
