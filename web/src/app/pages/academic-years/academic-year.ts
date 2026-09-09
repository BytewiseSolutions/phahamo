export interface AcademicYear {
  id: number;
  name: string;
  startDate: string;
  endDate: string;
  active: boolean;
}

export interface AcademicYearPayload {
  name: string;
  startDate: string;
  endDate: string;
  active: boolean;
}
