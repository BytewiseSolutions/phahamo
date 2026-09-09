export interface Term {
  id: number;
  academicYearId: number;
  academicYearName: string;
  name: string;
  startDate: string;
  endDate: string;
}

export interface TermPayload {
  academicYearId: number;
  name: string;
  startDate: string;
  endDate: string;
}
