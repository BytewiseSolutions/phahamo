import { EmploymentType } from './employment-type';

export interface Teacher {
  id: number;
  createdAt: string;
  updatedAt: string;
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  status: string;
  qualifications: string;
  employeeNumber: string;
  dateJoined: string;
  specialization: string;
  employmentType: EmploymentType;
}

export interface TeacherPayload {
  qualifications?: string;
  dateJoined?: string;
  specialization?: string;
  employmentType: EmploymentType;
}
