export interface StudentFeeLine {
  id: number;
  name: string;
  amount: number;
  mandatory: boolean;
  description?: string;
  fromAnnualFee: boolean;
}
