export interface StudentResultRow {
  id: number;
  studentId: number;
  studentFirstName: string;
  studentLastName: string;
  admissionNumber: string;
  subjectId: number;
  subjectName: string;
  schoolClassId: number;
  schoolClassName: string;
  termId: number;
  termName: string;
  finalMark: number;
  grade: string | null;
  remark: string | null;
  status: string;
  calculatedAt: string;
  finalisedAt: string | null;
}

export interface StudentResultsReport {
  studentId: number;
  firstName: string;
  lastName: string;
  admissionNumber: string;
  termId: number;
  termName: string;
  averageMark: number | null;
  overallGrade: string | null;
  overallRemark: string | null;
  subjects: StudentResultRow[];
}
