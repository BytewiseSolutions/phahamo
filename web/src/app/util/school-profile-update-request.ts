import { Currency } from './currency';
import { TermStructure } from './term-structure';

export interface SchoolProfileUpdateRequest {
  schoolName: string;
  schoolAddress: string;
  schoolContactEmail: string;
  schoolContactPhone: string;
  schoolTermStructure: TermStructure;
  schoolCurrency: Currency;
  schoolTimezone: string;
  absenceAlertsEnabled: boolean;
}
