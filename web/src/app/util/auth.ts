export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: number;
  fullName: string;
  roles: string[];
  schoolIds: number[];
  schoolName: string;
  mustChangePassword: boolean;
  requiresLegalAcceptance: boolean;
  termsVersion: string;
  privacyVersion: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface JwtPayload {
  roles?: string[];
  [key: string]: any;
}
