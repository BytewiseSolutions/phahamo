import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Term } from '../../terms/term';
import {StudentResultsReport} from '../results';

@Component({
  selector: 'app-my-results',
  standalone: false,
  templateUrl: './my-results.html',
  styleUrl: './my-results.scss'
})
export class MyResults implements OnInit {

  report: StudentResultsReport | null = null;

  terms: Term[] = [];
  selectedTermId: number | null = null;

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

        if (this.selectedTermId) {
          this.load();
        } else {
          this.loading = false;
        }
      },
      error: () => {
        this.errorMessage = 'Failed to load terms.';
      }
    });
  }

  load(): void {
    if (!this.selectedTermId) return;

    this.loading = true;
    this.errorMessage = '';

    this.backend.get<StudentResultsReport>(
      `/term-results/me?termId=${this.selectedTermId}`
    ).subscribe({
      next: (report) => {
        this.report = report;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load your results.';
        this.report = null;
        this.loading = false;
      }
    });
  }

  formatMark(value: number | null): string {
    if (value === null || value === undefined) return '—';
    return value.toFixed(1) + '%';
  }

  private iso(d: Date): string {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
