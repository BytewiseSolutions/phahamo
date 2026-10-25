import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { StudentFeePayment } from '../student-fee-payment';
import { PaymentPage } from './payment-page';
import { AuthService } from '../../../util/auth.service';

@Component({
  selector: 'app-payment-history',
  standalone: false,
  templateUrl: './payment-history.html',
  styleUrl: './payment-history.scss'
})
export class PaymentHistory implements OnInit {
  payments: StudentFeePayment[] = [];
  page = 0;
  totalPages = 0;
  totalElements = 0;
  loading = false;
  errorMessage = '';
  showReversalForm = false;
  selectedPayment: StudentFeePayment | null = null;

  get canReversePayments(): boolean {
    return this.authService.hasRole('SCHOOL_ADMIN')
      || this.authService.hasRole('SYSTEM_ADMIN')
      || this.authService.hasRole('FINANCE_OFFICER');
  }

  constructor(private backend: BackendService, private authService: AuthService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.errorMessage = '';
    this.backend.get<PaymentPage>(`/student-fees/payments?page=${this.page}&size=20`).subscribe({
      next: (result) => {
        const metadata = result.page ?? result;
        this.payments = result.content;
        this.page = metadata.number ?? 0;
        this.totalPages = metadata.totalPages ?? 0;
        this.totalElements = metadata.totalElements ?? 0;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load payment history.';
        this.loading = false;
      }
    });
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.load();
    }
  }

  nextPage(): void {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.load();
    }
  }

  openReversal(payment: StudentFeePayment): void {
    this.selectedPayment = payment;
    this.showReversalForm = true;
  }

  closeReversal(): void {
    this.showReversalForm = false;
    this.selectedPayment = null;
  }

  onReversed(): void {
    this.closeReversal();
    this.load();
  }

  formatDate(value: string): string {
    return new Date(value).toLocaleDateString('en-GB', {
      day: 'numeric', month: 'short', year: 'numeric'
    });
  }
}
