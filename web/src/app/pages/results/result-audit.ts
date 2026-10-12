export interface ResultAudit {
  id: number;
  termResultId: number;
  previousMark: number | null;
  newMark: number | null;
  reason: string;
  changedById: number | null;
  changedByName: string | null;
  changedAt: string;
}
