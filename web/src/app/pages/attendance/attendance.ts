import { AttendanceStatus } from './attendance-status';

export interface Attendance {
  id?: number;
  studentId: number;
  studentFirstName?: string;
  studentLastName?: string;
  schoolClassId?: number;
  schoolClassName?: string;
  date: string;
  status: AttendanceStatus | null;
}
