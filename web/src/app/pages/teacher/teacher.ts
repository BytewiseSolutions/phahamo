import { EmploymentType } from './employment-type';
import { User } from '../../util/user';

export interface Teacher {
  id: number;
  createdAt: string;
  updatedAt: string;
  user: User;
  qualifications: string;
  employeeNumber: string;
  dateJoined: string;
  specialization: string;
  employmentType: EmploymentType;
}

export interface TeacherPayload {
  user?: {
    id?: number;
    firstName?: string;
    lastName?: string;
    email?: string;
    phone?: string;
  };
  qualifications?: string;
  dateJoined?: string;
  specialization?: string;
  employmentType: EmploymentType;
}
