export interface PromotionHistoryRow {
  id: number;
  studentId: number;
  admissionNumber: string;
  studentFirstName: string;
  studentLastName: string;
  fromClassId: number | null;
  fromClassName: string | null;
  toClassId: number | null;
  toClassName: string | null;
  previousStatus: string;
  newStatus: string;
  reason: string;
  promotedAt: string;
  promotedByName: string | null;
  batchId: string;
}
