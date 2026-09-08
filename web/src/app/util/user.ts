export interface User {
  id: number;
  createdAt: string;
  updatedAt: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  status: string;
  roles: string[];
}

export interface NewUser {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  roles: string[];
}
