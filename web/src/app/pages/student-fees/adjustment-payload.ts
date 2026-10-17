import { AdjustmentType } from './adjustment-type';

export interface AdjustmentPayload {
  type: AdjustmentType;
  amount: number;
  reason: string;
}
