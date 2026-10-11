export interface Assessment {
  id: number;
  assignmentId: number;
  subjectId: number;
  subjectName: string;
  classId: number;
  className: string;
  academicYearId: number;
  academicYearName: string;
  termId: number;
  termName: string;
  teacherId: number | null;
  teacherName: string | null;
  name: string;
  maxMark: number;
  weight: number;
  date: string;
  createdAt?: string;
  updatedAt?: string;
}
