import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Term } from '../../terms/term';
import { StudentFeeAccount } from '../student-fee-account';

@Component({
  selector: 'app-my-fees',
  standalone: false,
  templateUrl: './my-fees.html',
  styleUrl: './my-fees.scss'
})
export class MyFees implements OnInit {

  terms: Term[] = [];
  selectedTermId: number | null = null;

  account: StudentFeeAccount | null = null;
  loading = false;
  errorMessage = '';

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.loadTerms();
  }

  loadTerms(): void {
    this.backend.get<Term[]>('/terms').subscribe({
      next: (terms) => {
        this.terms = terms;

        const today = this.iso(new Date());
        const current = terms.find(t =>
          t.startDate.substring(0, 10) <= today &&
          today <= t.endDate.substring(0, 10)
        );
        this.selectedTermId = (current ?? terms[0])?.id ?? null;

        if (this.selectedTermId) this.load();
      },
      error: () => this.errorMessage = 'Failed to load terms.'
    });
  }

  load(): void {
    if (!this.selectedTermId) return;

    this.loading = true;
    this.errorMessage = '';

    this.backend.get<StudentFeeAccount>(
      `/student-fees/me?termId=${this.selectedTermId}`
    ).subscribe({
      next: (acc) => {
        this.account = acc;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load your fee account.';
        this.account = null;
        this.loading = false;
      }
    });
  }

  onTermChange(): void {
    this.load();
  }

  formatAmount(value: number | null | undefined): string {
    if (value == null) return '—';
    return `M ${value.toFixed(2)}`;
  }

  formatDate(value: string): string {
    if (!value) return '—';
    try {
      return new Date(value).toLocaleDateString('en-GB', {
        day: 'numeric', month: 'short', year: 'numeric'
      });
    } catch {
      return value;
    }
  }

  paymentMethodLabel(method: string): string {
    const labels: Record<string, string> = {
      CASH: 'Cash',
      MOBILE_MONEY: 'Mobile Money',
      BANK_TRANSFER: 'Bank Transfer',
      CARD: 'Card'
    };
    return labels[method] || method;
  }

  private iso(d: Date): string {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
