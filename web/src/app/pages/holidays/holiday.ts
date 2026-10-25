export interface Holiday {
  id: number;
  name: string;
  startDate: string;
  endDate: string;
  recurring: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface HolidayPayload {
  name: string;
  startDate: string;
  endDate: string;
  recurring: boolean;
}
