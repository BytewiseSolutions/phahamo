import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Term } from '../../terms/term';
import { StudentFeeAccount } from '../student-fee-account';

type Tab = 'breakdown' | 'payments';

@Component({
  selector: 'app-my-fees',
  standalone: false,
  templateUrl: './my-fees.html',
  styleUrl: './my-fees.scss'
})
export class MyFees implements OnInit {

  terms: Term[] = [];
  selectedTermId: number | null = null;

  activeTab: Tab = 'breakdown';

  account: StudentFeeAccount | null = null;
  loading = false;
  errorMessage = '';

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.loadTerms();
  }

  setTab(tab: Tab): void {
    this.activeTab = tab;
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
        if (err.status === 404) {
          this.account = null;
          this.errorMessage = '';
        } else {
          this.errorMessage = err.error?.message || 'Failed to load your fee account.';
          this.account = null;
        }
        this.loading = false;
      }
    });
  }

  onTermChange(): void {
    this.load();
  }

  downloadReceipt(paymentId: number): void {
    const path = `/student-fees/payments/${paymentId}/receipt`;

    this.backend.downloadBlob(path).subscribe({
      next: (blob) => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = `receipt-${paymentId}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(a.href);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to download receipt.';
      }
    });
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
      MPESA: 'M-Pesa',
      ECOCASH: 'EcoCash',
      EFT: 'Bank Transfer (EFT)',
      CARD: 'Card'
    };
    return labels[method] || method;
  }

  getSelectedTermName(): string {
    const term = this.terms.find(t => t.id === this.selectedTermId);
    return term ? `${term.name} — ${term.academicYearName}` : 'this term';
  }

  private iso(d: Date): string {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
