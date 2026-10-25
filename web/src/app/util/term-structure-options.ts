import { TermStructure } from './term-structure';

export const TERM_STRUCTURE_OPTIONS: { value: TermStructure; label: string }[] = [
  { value: TermStructure.ONE_TERM, label: 'One term' },
  { value: TermStructure.TWO_TERM, label: 'Two terms' },
  { value: TermStructure.THREE_TERM, label: 'Three terms' },
  { value: TermStructure.FOUR_QUARTER, label: 'Four quarters' },
];
