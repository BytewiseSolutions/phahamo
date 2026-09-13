import {Gender} from './gender';
import {StudentStatus} from './student-status';

export interface Student {
  id: number;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: Gender;
  admissionNumber: string;
  status: StudentStatus;
  currentClassId?: number;
  currentClassName?: string;
}

export interface StudentPayload {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: Gender;
}
