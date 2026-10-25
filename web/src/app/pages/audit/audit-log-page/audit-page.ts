import { AuditLog } from './audit-log';

export interface AuditPage {
  content: AuditLog[];
  page?: { number: number; totalPages: number; totalElements: number };
  number?: number;
  totalPages?: number;
  totalElements?: number;
}
