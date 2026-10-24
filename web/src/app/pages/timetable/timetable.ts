import {DayOfWeek} from './day-of-week';

export interface Timetable {
  id: number;
  createdAt: string;
  updatedAt: string;
  dayOfWeek: DayOfWeek;
  periodId: number;
  periodName: string;
  periodStartTime: string;
  periodEndTime: string;
  assignmentId: number;
  subjectName: string;
  schoolClassName: string;
  teacherName: string;
  substituteTeacherName?: string | null;
  lessonStatus?: string | null;
  lessonDate?: string | null;
}

export interface TimetablePayload {
  dayOfWeek: DayOfWeek;
  periodId: number;
  assignmentId: number;
}
