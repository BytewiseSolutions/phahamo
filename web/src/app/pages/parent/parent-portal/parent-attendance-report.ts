import { ParentAttendanceSummary } from './parent-attendance-summary';
import { ParentAttendanceRecord } from './parent-attendance-record';

export interface ParentAttendanceReport {
  summary: ParentAttendanceSummary;
  records: ParentAttendanceRecord[];
}
