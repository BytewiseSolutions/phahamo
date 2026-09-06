export interface School {
  id: number;
  createdAt: string;
  updatedAt: string;
  schoolName: string;
  schoolAddress: string;
  schoolContactEmail: string;
  schoolContactPhone: string;
}

export interface SchoolRegistration {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phone: string;
  school: {
    schoolName: string;
    schoolAddress: string;
    schoolContactEmail: string;
    schoolContactPhone: string;
  };
}
