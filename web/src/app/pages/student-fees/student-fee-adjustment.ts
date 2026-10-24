import { AdjustmentType } from './adjustment-type';

export interface StudentFeeAdjustment {
  id: number;
  type: AdjustmentType;
  amount: number;
  reason: string;
  approvedById?: number;
  approvedByName?: string;
  appliedAt: string;
}

export type StudentFeeAdjustmentInput = Pick<
  StudentFeeAdjustment,
  'type' | 'amount' | 'reason'
>;
