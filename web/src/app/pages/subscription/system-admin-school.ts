export interface SystemAdminSchool {
  schoolId: number;
  schoolName: string;
  contactEmail: string;
  subscriptionId: number;
  plan: string;
  status: string;
  startedAt: string | null;
  endsAt: string | null;
  daysRemaining: number | null;
}
