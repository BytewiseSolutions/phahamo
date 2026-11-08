import { Gender } from './gender';
import { StudentStatus } from './student-status';

export interface Student {
  id: number;
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  dateOfBirth: string;
  gender: Gender;
  admissionNumber: string;
  status: StudentStatus;
  schoolClassId?: number;
  currentClassName?: string;
  photoUrl?: string;
}

export interface StudentPayload {
  dateOfBirth: string;
  gender: Gender;
  schoolClassId?: number | null;
}
