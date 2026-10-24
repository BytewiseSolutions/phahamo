export interface BulkFeeEmailResult {
  sentCount: number;
  skippedNoEmailCount: number;
  skippedPaidCount: number;
  failedCount: number;
}
