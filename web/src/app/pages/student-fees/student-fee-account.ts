import { StudentFeeLine } from './student-fee-line';
import { StudentFeeAdjustment } from './student-fee-adjustment';

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
  lines: StudentFeeLine[];
  adjustments: StudentFeeAdjustment[];
}
