export interface User {
  id: number;
  createdAt: string;
  updatedAt: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  dateOfBirth?: string | null;
  status: string;
  lockedUntil?: string | null;
  roles: string[];
  student?: import('../pages/student/student').Student | null;
}
