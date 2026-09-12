export interface SubjectClassAssignment {
  id: number;
  academicYearId: number;
  academicYearName: string;
  subjectId: number;
  subjectName: string;
  schoolClassId: number;
  schoolClassName: string;
  teacherId?: number;
  teacherName?: string;
}

export interface SubjectClassAssignmentPayload {
  academicYearId: number;
  subjectId: number;
  schoolClassId: number;
  teacherId: number | null;
}
