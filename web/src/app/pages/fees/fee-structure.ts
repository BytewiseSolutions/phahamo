import { FeeItem } from './fee-item';

export interface FeeStructure {
  id: number;
  classId: number;
  className: string;
  termId: number;
  termName: string;
  academicYearName: string;
  items: FeeItem[];
  total: number;
  mandatoryTotal: number;
}
