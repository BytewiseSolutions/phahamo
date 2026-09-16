export interface Period {
  id: number;
  createdAt: string;
  updatedAt: string;
  name: string;
  startTime: string;
  endTime: string;
}

export interface PeriodPayload {
  name: string;
  startTime: string;
  endTime: string;
}
