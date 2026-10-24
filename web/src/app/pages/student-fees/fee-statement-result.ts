export interface FeeStatementResult {
  sentCount: number;
  skippedNoEmailCount: number;
  skippedPaidCount: number;
  failedCount: number;
  skippedNoEmail: string[];
  skippedPaid: string[];
  failed: string[];
}
