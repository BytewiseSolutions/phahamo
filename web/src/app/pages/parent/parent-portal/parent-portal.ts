import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { Term } from '../../terms/term';
import { ParentAttendanceReport } from './parent-attendance-report';
import { ParentChild } from './parent-child';
import { ParentDashboard } from './parent-dashboard';
import { ParentFeeAccount } from './parent-fee-account';
import { ParentResult } from './parent-result';

@Component({
  selector: 'app-parent-portal',
  standalone: false,
  templateUrl: './parent-portal.html',
  styleUrl: './parent-portal.scss'
})
export class ParentPortal implements OnInit {

  section = 'children';

  children: ParentChild[] = [];
  dashboard: ParentDashboard | null = null;
  selectedDashboardChildId: number | null = null;
  terms: Term[] = [];
  selectedChildId: number | null = null;
  selectedTermId: number | null = null;

  attendance: ParentAttendanceReport | null = null;
  results: ParentResult[] = [];
  fees: ParentFeeAccount | null = null;

  loading = false;
  downloadingReportCard = false;
  errorMessage = '';

  constructor(
    private backend: BackendService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.section = this.route.snapshot.data['section'] ?? 'children';
    if (this.section === 'children') {
      this.loadDashboard();
    } else {
      this.loadChildren();
    }
  }

  loadDashboard(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<ParentDashboard>('/parent/dashboard').subscribe({
      next: (dashboard) => {
        this.dashboard = dashboard;
        this.children = dashboard.children.map(summary => summary.child);
        this.selectedDashboardChildId = dashboard.children[0]?.child.id ?? null;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load your parent dashboard.';
        this.loading = false;
      }
    });
  }

  loadChildren(): void {
    this.backend.get<ParentChild[]>('/parent/children').subscribe({
      next: (children) => {
        this.children = children;
        this.selectedChildId = children[0]?.id ?? null;
        this.loadTerms();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load your children.';
      }
    });
  }

  loadTerms(): void {
    this.backend.get<Term[]>('/terms').subscribe({
      next: (terms) => {
        this.terms = terms;

        const today = this.iso(new Date());
        const currentTerm = terms.find(term =>
          term.startDate.substring(0, 10) <= today &&
          today <= term.endDate.substring(0, 10)
        );

        this.selectedTermId = (currentTerm ?? terms[0])?.id ?? null;
        this.load();
      },
      error: () => {
        this.errorMessage = 'Failed to load terms.';
      }
    });
  }

  load(): void {
    if (!this.selectedChildId || !this.selectedTermId || this.section === 'children') {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const path = `/parent/children/${this.selectedChildId}/${this.section}`
      + `?termId=${this.selectedTermId}`;

    this.backend.get<ParentAttendanceReport | ParentResult[] | ParentFeeAccount>(path).subscribe({
      next: (value) => {
        if (this.section === 'attendance') {
          this.attendance = value as ParentAttendanceReport;
        } else if (this.section === 'results') {
          this.results = value as ParentResult[];
        } else {
          this.fees = value as ParentFeeAccount;
        }
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || `Failed to load ${this.section}.`;
        this.loading = false;
      }
    });
  }

  formatAmount(value: number | null | undefined): string {
    if (value == null) {
      return '—';
    }
    return `M ${value.toFixed(2)}`;
  }

  attendanceLabel(status: string): string {
    return {
      PRESENT: 'Present',
      ABSENT: 'Absent',
      LATE: 'Late',
      EXCUSED: 'Excused',
      NOT_MARKED: 'Not marked'
    }[status] ?? 'Not marked';
  }

  selectDashboardChild(childId: number): void {
    this.selectedDashboardChildId = childId;
  }

  downloadReportCard(): void {
    if (!this.selectedChildId || !this.selectedTermId || this.results.length === 0) {
      return;
    }

    this.downloadingReportCard = true;
    this.backend.downloadBlob(
      `/parent/children/${this.selectedChildId}/report-card?termId=${this.selectedTermId}`
    ).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `report-card-${this.selectedChildId}-term-${this.selectedTermId}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
        this.downloadingReportCard = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to download report card.';
        this.downloadingReportCard = false;
      }
    });
  }

  formatDate(value: string): string {
    if (!value) {
      return '—';
    }

    return new Date(value).toLocaleDateString('en-GB', {
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    });
  }

  private iso(date: Date): string {
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
      + `-${String(date.getDate()).padStart(2, '0')}`;
  }
}
