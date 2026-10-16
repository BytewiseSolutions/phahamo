import { TermResultStatus } from './term-result-status';

export interface TermResult {
  id: number;
  studentId: number;
  studentFirstName: string;
  studentLastName: string;
  admissionNumber: string;

  subjectId: number;
  subjectName: string;

  classId: number;
  className: string;

  termId: number;
  termName: string;

  finalMark: number;
  grade: string | null;
  remark: string | null;

  status: TermResultStatus;
  calculatedAt: string;
  finalisedAt: string | null;
  finalisedById: number | null;
  finalisedByName: string | null;
}
