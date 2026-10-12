export interface LessonAttendanceSummary {
  lessonId: number;
  date: string;
  periodName: string;
  subjectName: string;
  className: string;
  total: number;
  present: number;
  absent: number;
  late: number;
  excused: number;
  presentPercent: number;
  absentPercent: number;
  latePercent: number;
  excusedPercent: number;
}

export interface StudentAttendanceSummary {
  studentId: number;
  firstName: string;
  lastName: string;
  admissionNumber: string;
  total: number;
  present: number;
  absent: number;
  late: number;
  excused: number;
  presentPercent: number;
  absentPercent: number;
  latePercent: number;
  excusedPercent: number;
}

export interface StudentAttendanceReport {
  summary: StudentAttendanceSummary;
  records: {
    id: number;
    studentId: number;
    studentFirstName: string;
    studentLastName: string;
    schoolClassId: number;
    schoolClassName: string;
    lessonId: number;
    date: string;
    status: string;
  }[];
}
