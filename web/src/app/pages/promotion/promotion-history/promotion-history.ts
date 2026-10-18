import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';

interface PromotionRow {
  id: number;
  studentId: number;
  admissionNumber: string;
  studentFirstName: string;
  studentLastName: string;
  fromClassId: number | null;
  fromClassName: string | null;
  toClassId: number | null;
  toClassName: string | null;
  previousStatus: string;
  newStatus: string;
  reason: string;
  promotedAt: string;
  promotedByName: string | null;
  batchId: string;
}

@Component({
  selector: 'app-promotion-history',
  standalone: false,
  templateUrl: './promotion-history.html',
  styleUrl: './promotion-history.scss'
})
export class PromotionHistory implements OnInit {

  rows: PromotionRow[] = [];
  loading = false;
  errorMessage = '';

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<PromotionRow[]>('/promotions/history').subscribe({
      next: (res) => {
        this.rows = res;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load promotion history.';
        this.loading = false;
      }
    });
  }

  formatDate(value: string): string {
    if (!value) return '—';
    try {
      return new Date(value).toLocaleDateString('en-GB', {
        day: 'numeric', month: 'short', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
      });
    } catch {
      return value;
    }
  }
}
