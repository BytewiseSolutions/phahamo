export type PeriodType = 'LESSON' | 'BREAK' | 'LUNCH';

export interface Period {
  id: number;
  createdAt: string;
  updatedAt: string;
  name: string;
  type: PeriodType;
  startTime: string;
  endTime: string;
}

export interface PeriodPayload {
  name: string;
  type: PeriodType;
  startTime: string;
  endTime: string;
}
