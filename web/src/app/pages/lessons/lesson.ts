export interface Lesson {
  id: number;
  timetableId: number;
  date: string;
  dayOfWeek: string;
  periodName: string;
  periodStartTime: string;
  periodEndTime: string;
  subjectName: string;
  schoolClassName: string;
  teacherName: string;
  substituteTeacherName: string | null;
  status: string;
}
