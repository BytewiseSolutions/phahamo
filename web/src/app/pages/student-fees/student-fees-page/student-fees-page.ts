import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import { SchoolClass } from '../../class/class';
import { Term } from '../../terms/term';
import { StudentFeeAccount } from '../student-fee-account';

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

  accounts: StudentFeeAccount[] = [];

  loading = false;
  generating = false;
  errorMessage = '';

  constructor(
    private backend: BackendService,
    private router: Router,
    private confirmDialog: ConfirmDialogService
  ) {}

  ngOnInit(): void {
    this.loadFilters();
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
      title: 'Generate Fee Accounts',
      message: 'Create fee accounts for all students in this class? Students with existing accounts will be skipped.',
      confirmText: 'Generate',
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
