export interface AuditLog {
  id: number;
  action: string;
  actorId: number | null;
  actorName: string;
  targetType: string;
  targetId: number | null;
  summary: string;
  occurredAt: string;
}
