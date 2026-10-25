export interface ParentPayment {
  id: number;
  amount: number;
  paidAt: string;
  method: string;
  reference?: string;
}
