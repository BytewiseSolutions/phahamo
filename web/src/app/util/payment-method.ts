export enum PaymentMethodType {
  MPESA = 'MPESA',
  ECOCASH = 'ECOCASH',
  EFT = 'EFT',
  CARD = 'CARD'
}

export interface PaymentMethod {
  id: number;
  type: PaymentMethodType;
  label: string;
  details: string;
  active: boolean;
  sortOrder: number;
}
