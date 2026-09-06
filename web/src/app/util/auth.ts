export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: string;
  fullName: string;
  role: string;
  schoolId: string;
}
