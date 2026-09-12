import {Gender} from './gender';

export interface Student {
  id: number;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: Gender;
  admissionNumber: string;
  status: string;
}

export interface StudentPayload {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
  status?: string;
}
