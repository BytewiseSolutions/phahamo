export interface SchoolClass {
  id: number;
  name: string;
  gradeLevel: number;
  academicYearId: number;
  academicYearName: string;
  classTeacherId?: number;
  classTeacherName?: string;
}

export interface SchoolClassPayload {
  name: string;
  gradeLevel: number;
  academicYearId: number;
  classTeacherId: number | null;
}
