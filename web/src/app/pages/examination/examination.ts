export interface ExaminationSubjectClass {
  subjectId: number;
  subjectName: string;
  classId: number;
  className: string;
}

export interface Examination {
  id: number;
  termId: number;
  termName: string;
  name: string;
  startDate: string;
  endDate: string;
  subjectClasses: ExaminationSubjectClass[];
  createdAt?: string;
  updatedAt?: string;
}

export interface ExaminationPayload {
  termId: number | null;
  name: string;
  startDate: string;
  endDate: string;
  subjectClasses: { subjectId: number; classId: number }[];
}
