import { Gender } from './gender';
import { StudentStatus } from './student-status';

export interface Student {
  id: number;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: Gender;
  admissionNumber: string;
  status: StudentStatus;
  schoolClassId?: number;
  currentClassName?: string;
}

export interface StudentPayload {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: Gender;
  schoolClassId?: number | null;
}
