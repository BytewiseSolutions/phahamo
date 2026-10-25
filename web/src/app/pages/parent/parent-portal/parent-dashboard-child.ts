import { ParentChild } from './parent-child';

export interface ParentDashboardChild {
  child: ParentChild;
  todayAttendance: string;
  feeBalance: number | null;
}
