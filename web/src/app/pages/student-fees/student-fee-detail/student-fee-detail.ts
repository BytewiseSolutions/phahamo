import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import { StudentFeeAccount } from '../student-fee-account';
import { StudentFeeAdjustment } from '../student-fee-adjustment';
import {BulkFeeEmailResult} from '../bulk-fee-email-result';
import { StudentFeePayment } from '../student-fee-payment';
import { AuthService } from '../../../util/auth.service';

type Tab = 'breakdown' | 'adjustments' | 'payments';

@Component({
  selector: 'app-student-fee-detail',
  standalone: false,
  templateUrl: './student-fee-detail.html',
  styleUrl: './student-fee-detail.scss'
})
export class StudentFeeDetail implements OnInit {

  account: StudentFeeAccount | null = null;
  loading = false;
  errorMessage = '';

  activeTab: Tab = 'breakdown';

  showAdjustmentForm = false;
  showPaymentForm = false;
  showReversalForm = false;
  selectedPayment: StudentFeePayment | null = null;
  accountId: number | null = null;

  sendingStatement = false;
  statementResult: BulkFeeEmailResult | null = null;

  constructor(
    private backend: BackendService,
    private route: ActivatedRoute,
    private router: Router,
    private confirmDialog: ConfirmDialogService,
    private authService: AuthService
  ) {}

  get canReversePayments(): boolean {
    return this.authService.hasRole('SCHOOL_ADMIN')
      || this.authService.hasRole('SYSTEM_ADMIN')
      || this.authService.hasRole('FINANCE_OFFICER');
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('accountId');
    this.accountId = idParam ? Number(idParam) : null;
    this.load();
  }

  setTab(tab: Tab): void {
    this.activeTab = tab;
  }

  load(): void {
    if (!this.accountId) {
      this.errorMessage = 'Invalid account ID.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.backend.get<StudentFeeAccount>(`/student-fees/${this.accountId}`).subscribe({
      next: (acc) => {
        this.account = acc;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load account.';
        this.account = null;
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/student-fees']);
  }

  openAdjustmentForm(): void {
    this.showAdjustmentForm = true;
  }

  onAdjustmentClosed(): void {
    this.showAdjustmentForm = false;
  }

  onAdjustmentSaved(): void {
    this.showAdjustmentForm = false;
    this.load();
  }

  openPaymentForm(): void {
    this.showPaymentForm = true;
  }

  onPaymentClosed(): void {
    this.showPaymentForm = false;
  }

  onPaymentSaved(): void {
    this.showPaymentForm = false;
    this.load();
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

  async removeAdjustment(adj: StudentFeeAdjustment): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Remove Adjustment',
      message: `Remove the ${adj.type.toLowerCase()} of M ${adj.amount.toFixed(2)}? The student's balance will increase.`,
      confirmText: 'Remove',
      danger: true
    });
    if (!confirmed) return;

    this.backend.delete<StudentFeeAccount>(`/student-fees/adjustments/${adj.id}`).subscribe({
      next: () => this.load(),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to remove adjustment.';
      }
    });
  }

  async emailStatement(): Promise<void> {
    if (!this.account || !this.accountId) return;

    const confirmed = await this.confirmDialog.confirm({
      title: 'Email Fee Statement',
      message: `Email a PDF fee statement to all parents of ${this.account.studentFirstName} ${this.account.studentLastName} with an email address?`,
      confirmText: 'Send',
      danger: false
    });
    if (!confirmed) return;

    this.sendingStatement = true;
    this.statementResult = null;
    this.errorMessage = '';

    this.backend.postSilently<BulkFeeEmailResult>(
      `/fee-statements/account/${this.accountId}/email`, {}
    ).subscribe({
      next: (result) => {
        this.statementResult = result;
        this.sendingStatement = false;
        this.load();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to email statement.';
        this.sendingStatement = false;
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

  typeLabel(type: string): string {
    return type.charAt(0) + type.slice(1).toLowerCase();
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
}
