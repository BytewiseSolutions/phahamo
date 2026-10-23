import { ImportSummary } from './import-summary';
import { ImportRowResult } from './import-row-result';

export interface ImportResult {
  summary: ImportSummary;
  validRows: ImportRowResult[];
  invalidRows: ImportRowResult[];
}
