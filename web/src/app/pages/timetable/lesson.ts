import { DayOfWeek } from './day-of-week';
import { LessonStatus } from './lesson-status';

export interface Lesson {
  id: number;
  createdAt: string;
  updatedAt: string;
  timetableId: number;
  dayOfWeek: DayOfWeek;
  periodName: string;
  periodStartTime: string;
  periodEndTime: string;
  subjectName: string;
  schoolClassName: string;
  teacherName: string;
  substituteTeacherName: string | null;
  date: string;
  status: LessonStatus;
}
