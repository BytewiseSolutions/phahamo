import { StudentClassFeePreviewLine } from './student-class-fee-preview-line';

export interface StudentClassFeePreview {
  studentId: number;
  currentClassName?: string;
  newClassName: string;
  feesChanged: boolean;
  terms: StudentClassFeePreviewLine[];
}
