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
  schoolLogoId: number | null;
}

export interface SchoolRegistration {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phone: string;
  school: Pick<School, 'schoolName' | 'schoolAddress' | 'schoolContactEmail' | 'schoolContactPhone'>;
}

export interface SchoolProfileUpdateRequest {
  schoolName: string;
  schoolAddress: string;
  schoolContactEmail: string;
  schoolContactPhone: string;
  schoolTermStructure: TermStructure;
  schoolCurrency: Currency;
  schoolTimezone: string;
}

export enum TermStructure {
  ONE_TERM = 'ONE_TERM',
  TWO_TERM = 'TWO_TERM',
  THREE_TERM = 'THREE_TERM',
  FOUR_QUARTER = 'FOUR_QUARTER',
}

export enum Currency {
  LSL = 'LSL',
  ZAR = 'ZAR',
  USD = 'USD',
}

export const TERM_STRUCTURE_OPTIONS: { value: TermStructure; label: string }[] = [
  { value: TermStructure.ONE_TERM, label: 'One term' },
  { value: TermStructure.TWO_TERM, label: 'Two terms' },
  { value: TermStructure.THREE_TERM, label: 'Three terms' },
  { value: TermStructure.FOUR_QUARTER, label: 'Four quarters' },
];

export const CURRENCY_OPTIONS: { value: Currency; label: string }[] = [
  { value: Currency.LSL, label: 'LSL — Lesotho Loti' },
  { value: Currency.ZAR, label: 'ZAR — South African Rand' },
  { value: Currency.USD, label: 'USD — US Dollar' },
];
