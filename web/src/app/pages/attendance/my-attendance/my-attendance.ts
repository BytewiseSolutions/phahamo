import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { StudentAttendanceReport } from '../attendance-report';
import { Term } from '../../terms/term';

type RangeMode = 'term' | 'custom';

@Component({
  selector: 'app-my-attendance',
  standalone: false,
  templateUrl: './my-attendance.html',
  styleUrl: './my-attendance.scss'
})
export class MyAttendance implements OnInit {

  report: StudentAttendanceReport | null = null;

  terms: Term[] = [];
  selectedTermId: number | null = null;

  rangeMode: RangeMode = 'term';
  fromDate = '';
  toDate = '';

  loading = false;
  errorMessage = '';

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    const today = new Date();
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(today.getDate() - 30);

    this.toDate = this.iso(today);
    this.fromDate = this.iso(thirtyDaysAgo);

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

        this.rangeMode = this.selectedTermId ? 'term' : 'custom';
        this.load();
      },
      error: () => {
        this.rangeMode = 'custom';
        this.load();
      }
    });
  }

  setRangeMode(mode: RangeMode): void {
    this.rangeMode = mode;
    this.load();
  }

  load(): void {
    this.errorMessage = '';

    if (this.rangeMode === 'term') {
      if (!this.selectedTermId) return;

      this.loading = true;
      this.backend.get<StudentAttendanceReport>(
        `/attendance/report/me?termId=${this.selectedTermId}`
      ).subscribe({
        next: (report) => {
          this.report = report;
          this.loading = false;
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to load your attendance.';
          this.report = null;
          this.loading = false;
        }
      });
      return;
    }

    if (!this.fromDate || !this.toDate) return;

    if (this.fromDate > this.toDate) {
      this.errorMessage = 'From date must be before To date.';
      this.report = null;
      return;
    }

    this.loading = true;
    this.backend.get<StudentAttendanceReport>(
      `/attendance/report/me?from=${this.fromDate}&to=${this.toDate}`
    ).subscribe({
      next: (report) => {
        this.report = report;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load your attendance.';
        this.report = null;
        this.loading = false;
      }
    });
  }

  formatDate(value: string): string {
    if (!value) return '—';
    try {
      return new Date(value).toLocaleDateString('en-GB', {
        day: 'numeric', month: 'short', year: 'numeric', timeZone: 'UTC'
      });
    } catch {
      return value;
    }
  }

  formatPercent(value: number): string {
    return `${value.toFixed(1)}%`;
  }

  statusLabel(status: string): string {
    return status.charAt(0) + status.slice(1).toLowerCase();
  }

  private iso(d: Date): string {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
