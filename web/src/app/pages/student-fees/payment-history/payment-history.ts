import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
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
export class PaymentHistory implements OnInit, OnDestroy {
  payments: StudentFeePayment[] = [];
  page = 0;
  totalPages = 0;
  totalElements = 0;
  loading = false;
  errorMessage = '';
  searchTerm = '';
  showReversalForm = false;
  selectedPayment: StudentFeePayment | null = null;
  private searchSubject = new Subject<void>();
  private destroy$ = new Subject<void>();

  get canReversePayments(): boolean {
    return this.authService.hasRole('SCHOOL_ADMIN')
      || this.authService.hasRole('SYSTEM_ADMIN')
      || this.authService.hasRole('FINANCE_OFFICER');
  }

  constructor(private backend: BackendService, private authService: AuthService) {}

  ngOnInit(): void {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.page = 0;
      this.load();
    });
    this.load();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  load(): void {
    this.loading = true;
    this.errorMessage = '';
    const search = this.searchTerm.trim() ? `&search=${encodeURIComponent(this.searchTerm.trim())}` : '';
    this.backend.get<PaymentPage>(`/student-fees/payments?page=${this.page}&size=20${search}`).subscribe({
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

  onSearchChange(): void {
    this.searchSubject.next();
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
