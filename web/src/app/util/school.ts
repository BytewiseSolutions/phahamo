import { Currency } from './currency';
import { TermStructure } from './term-structure';

export interface School {
  id: number;
  createdAt: string;
  updatedAt: string;
  schoolName: string;
  schoolAddress: string;
  schoolContactEmail: string;
  schoolContactPhone: string;
  schoolTermStructure: TermStructure;
  schoolCurrency: Currency;
  schoolTimezone: string;
  absenceAlertsEnabled: boolean;
  schoolLogoId: number | null;
}
