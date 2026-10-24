import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { Term } from '../../terms/term';

interface ParentChild {
  id: number;
  name: string;
  admissionNumber: string;
  className?: string;
}

interface AttendanceReport {
  summary: {
    total: number;
    present: number;
    absent: number;
    late: number;
    excused: number;
    presentPercent: number;
  };
}

interface FeeAccount {
  totalFees: number;
  totalAdjustments: number;
  amountPaid: number;
  balance: number;
  payments: {
    id: number;
    amount: number;
    paidAt: string;
    method: string;
    reference?: string;
  }[];
}

interface ParentResult {
  subjectName: string;
  finalMark: number;
  grade: string;
  remark: string;
}

@Component({
  selector: 'app-parent-portal',
  standalone: false,
  templateUrl: './parent-portal.html',
  styleUrl: './parent-portal.scss'
})
export class ParentPortal implements OnInit {

  section = 'children';

  children: ParentChild[] = [];
  terms: Term[] = [];
  selectedChildId: number | null = null;
  selectedTermId: number | null = null;

  attendance: AttendanceReport | null = null;
  results: ParentResult[] = [];
  fees: FeeAccount | null = null;

  loading = false;
  errorMessage = '';

  constructor(
    private backend: BackendService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.section = this.route.snapshot.data['section'] ?? 'children';
    this.loadChildren();
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

    this.backend.get<AttendanceReport | ParentResult[] | FeeAccount>(path).subscribe({
      next: (value) => {
        if (this.section === 'attendance') {
          this.attendance = value as AttendanceReport;
        } else if (this.section === 'results') {
          this.results = value as ParentResult[];
        } else {
          this.fees = value as FeeAccount;
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
