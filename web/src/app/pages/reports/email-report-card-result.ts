export interface EmailReportCardResult {
  sentCount: number;
  skippedNoEmailCount: number;
  skippedNotFinalisedCount: number;
  failedCount: number;
  skippedNoEmail: string[];
  skippedNotFinalised: string[];
  failed: string[];
}
