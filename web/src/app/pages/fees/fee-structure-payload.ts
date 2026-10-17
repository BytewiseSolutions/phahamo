import { FeeItem } from './fee-item';

export interface FeeStructurePayload {
  classId: number;
  termId: number;
  items: FeeItem[];
}
