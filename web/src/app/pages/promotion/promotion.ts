import {PromotionAction} from './promotion-action';

export interface PromotionPayload {
  fromClassId: number;
  toClassId?: number;
  action: PromotionAction;
  studentIds: number[];
  reason: string;
}

export interface PromotionResponse {
  batchId: string;
}
