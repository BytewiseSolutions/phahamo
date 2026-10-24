import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import { SchoolClass } from '../../class/class';
import { Term } from '../../terms/term';
import { StudentFeeAccount } from '../student-fee-account';
import {BulkFeeEmailResult} from '../bulk-fee-email-result';
import {SendReminderRequest} from '../send-reminder-request';

type StatusFilter = 'all' | 'owing' | 'paid';

@Component({
  selector: 'app-student-fees-page',
  standalone: false,
  templateUrl: './student-fees-page.html',
  styleUrl: './student-fees-page.scss'
})
export class StudentFeesPage implements OnInit {

  classes: SchoolClass[] = [];
  terms: Term[] = [];

  selectedClassId: number | null = null;
  selectedTermId: number | null = null;
  statusFilter: StatusFilter = 'all';

  accounts: StudentFeeAccount[] = [];

  loading = false;
  generating = false;
  errorMessage = '';

  selectedAccountIds = new Set<number>();
  sending = false;
  lastResult: BulkFeeEmailResult | null = null;

  sendingStatements = false;
  lastStatementResult: BulkFeeEmailResult | null = null;

  constructor(
    private backend: BackendService,
    private router: Router,
    private confirmDialog: ConfirmDialogService
  ) {}

  ngOnInit(): void {
    this.loadFilters();
  }

  toggleSelection(accountId: number): void {
    if (this.selectedAccountIds.has(accountId)) {
      this.selectedAccountIds.delete(accountId);
    } else {
      this.selectedAccountIds.add(accountId);
    }
  }

  isSelected(accountId: number): boolean {
    return this.selectedAccountIds.has(accountId);
  }

  get sendableCount(): number {
    return this.filteredAccounts.filter(a => a.balance > 0 && a.hasEmailParent).length;
  }

  get selectedCount(): number {
    return this.selectedAccountIds.size;
  }

  get allSelected(): boolean {
    return this.sendableCount > 0 && this.selectedAccountIds.size === this.sendableCount;
  }

  toggleAll(): void {
    if (this.allSelected) {
      this.selectedAccountIds.clear();
    } else {
      this.filteredAccounts
        .filter(a => a.balance > 0 && a.hasEmailParent)
        .forEach(a => this.selectedAccountIds.add(a.id));
    }
  }

  async sendSelected(): Promise<void> {
    if (this.selectedAccountIds.size === 0) return;

    const count = this.selectedAccountIds.size;
    const confirmed = await this.confirmDialog.confirm({
      title: 'Send Fee Reminders',
      message: `Send reminder emails to ${count} selected ${count === 1 ? 'parent' : 'parents'}?\n\nEach email shows the student name and outstanding balance.`,
      confirmText: `Send ${count}`,
      danger: false
    });
    if (!confirmed) return;

    this.sending = true;
    this.errorMessage = '';
    this.lastResult = null;

    const payload: SendReminderRequest = {
      accountIds: Array.from(this.selectedAccountIds)
    };

    this.backend.postSilently<BulkFeeEmailResult>('/fee-reminders/send-selected', payload).subscribe({
      next: (result) => {
        this.lastResult = result;
        this.sending = false;
        this.selectedAccountIds.clear();
        this.load();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to send reminders.';
        this.sending = false;
      }
    });
  }

  async sendAllOwing(): Promise<void> {
    if (!this.selectedTermId) return;

    const confirmed = await this.confirmDialog.confirm({
      title: 'Send Reminders to All Owing',
      message: `Send reminder emails to every parent of a student with an outstanding balance this term?\n\nStudents without parent email addresses will be skipped and listed.`,
      confirmText: 'Send All',
      danger: false
    });
    if (!confirmed) return;

    this.sending = true;
    this.errorMessage = '';
    this.lastResult = null;

    this.backend.postSilently<BulkFeeEmailResult>(
      `/fee-reminders/send-all?termId=${this.selectedTermId}`, {}
    ).subscribe({
      next: (result) => {
        this.lastResult = result;
        this.sending = false;
        this.load();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to send reminders.';
        this.sending = false;
      }
    });
  }

  formatLastSent(value: string | null): string {
    if (!value) return 'Never';
    try {
      const date = new Date(value);
      const days = Math.floor((Date.now() - date.getTime()) / (1000 * 60 * 60 * 24));
      if (days === 0) return 'Today';
      if (days === 1) return 'Yesterday';
      if (days < 30) return `${days} days ago`;
      return date.toLocaleDateString('en-GB', {
        day: 'numeric', month: 'short', year: 'numeric'
      });
    } catch {
      return value;
    }
  }

  lastSentTone(value: string | null): 'ok' | 'warn' | 'danger' {
    if (!value) return 'ok';
    try {
      const days = Math.floor((Date.now() - new Date(value).getTime()) / (1000 * 60 * 60 * 24));
      if (days < 3) return 'danger';
      if (days < 7) return 'warn';
      return 'ok';
    } catch {
      return 'ok';
    }
  }

  get filteredAccounts(): StudentFeeAccount[] {
    if (this.statusFilter === 'owing') {
      return this.accounts.filter(a => a.balance > 0);
    }
    if (this.statusFilter === 'paid') {
      return this.accounts.filter(a => a.balance <= 0);
    }
    return this.accounts;
  }

  loadFilters(): void {
    this.backend.get<SchoolClass[]>('/classes').subscribe({
      next: (classes) => {
        this.classes = classes;
        this.selectedClassId = classes[0]?.id ?? null;

        this.backend.get<Term[]>('/terms').subscribe({
          next: (terms) => {
            this.terms = terms;
            this.selectedTermId = terms[0]?.id ?? null;
            this.load();
          },
          error: () => this.errorMessage = 'Failed to load terms.'
        });
      },
      error: () => this.errorMessage = 'Failed to load classes.'
    });
  }

  load(): void {
    if (!this.selectedClassId || !this.selectedTermId) return;

    this.loading = true;
    this.errorMessage = '';

    const url = `/student-fees/class/${this.selectedClassId}?termId=${this.selectedTermId}`;

    this.backend.get<StudentFeeAccount[]>(url).subscribe({
      next: (rows) => {
        this.accounts = rows.sort((a, b) =>
          a.admissionNumber.localeCompare(b.admissionNumber, undefined, { numeric: true })
        );
        this.selectedAccountIds.clear();
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load student fee accounts.';
        this.accounts = [];
        this.loading = false;
      }
    });
  }

  onFilterChange(): void {
    this.load();
  }

  async generateForClass(): Promise<void> {
    if (!this.selectedClassId || !this.selectedTermId) return;

    const confirmed = await this.confirmDialog.confirm({
      title: 'Sync Fee Accounts',
      message: 'Create missing accounts and update unpaid accounts using the current fee structure?',
      confirmText: 'Sync',
      danger: false
    });
    if (!confirmed) return;

    this.generating = true;
    this.errorMessage = '';

    const url = `/student-fees/generate/class/${this.selectedClassId}?termId=${this.selectedTermId}`;

    this.backend.postSilently<StudentFeeAccount[]>(url, {}).subscribe({
      next: () => {
        this.generating = false;
        this.load();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to generate accounts.';
        this.generating = false;
      }
    });
  }

  openDetail(account: StudentFeeAccount): void {
    this.router.navigate(['/student-fees', account.id]);
  }

  async sendStatementsAllOwing(): Promise<void> {
    if (!this.selectedTermId) return;

    const confirmed = await this.confirmDialog.confirm({
      title: 'Email Fee Statements',
      message: `Email PDF fee statements to every parent of a student with an outstanding balance this term?\n\nStudents without parent email addresses will be skipped and listed.`,
      confirmText: 'Send All',
      danger: false
    });
    if (!confirmed) return;

    this.sendingStatements = true;
    this.errorMessage = '';
    this.lastStatementResult = null;

    this.backend.postSilently<BulkFeeEmailResult>(
      `/fee-statements/term/${this.selectedTermId}/email-all`, {}
    ).subscribe({
      next: (result) => {
        this.lastStatementResult = result;
        this.sendingStatements = false;
        this.load();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to send statements.';
        this.sendingStatements = false;
      }
    });
  }

  formatAmount(value: number | null | undefined): string {
    if (value == null) return '—';
    return `M ${value.toFixed(2)}`;
  }

  balanceClass(value: number): string {
    if (value <= 0) return 'paid';
    if (value >= 500) return 'high';
    return 'medium';
  }
}
