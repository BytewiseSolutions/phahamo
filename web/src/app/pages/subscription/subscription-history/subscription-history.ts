import { Component, Input, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { AuthService } from '../../../util/auth.service';
import { SubscriptionRequest } from '../../../util/subscription-request';

@Component({
  selector: 'app-subscription-history',
  standalone: false,
  templateUrl: './subscription-history.html',
  styleUrl: './subscription-history.scss'
})
export class SubscriptionHistory implements OnInit {

  @Input() schools: { id: number; name: string }[] = [];

  requestHistory: SubscriptionRequest[] = [];
  historySchoolId: number | null = null;
  loading = false;
  errorMessage = '';

  isSystemAdmin = false;

  constructor(private backend: BackendService, private authService: AuthService) {
    this.isSystemAdmin = this.authService.hasRole('SYSTEM_ADMIN');
  }

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory(): void {
    this.loading = true;
    this.errorMessage = '';

    if (!this.isSystemAdmin) {
      this.backend.get<SubscriptionRequest[]>('/subscription-requests/my-school').subscribe({
        next: (reqs) => {
          this.requestHistory = reqs;
          this.loading = false;
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to load history.';
          this.loading = false;
        }
      });
      return;
    }

    const url = this.historySchoolId
      ? `/subscription-requests/school/${this.historySchoolId}`
      : `/subscription-requests`;

    this.backend.get<SubscriptionRequest[]>(url).subscribe({
      next: (reqs) => {
        this.requestHistory = reqs;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load history.';
        this.loading = false;
      }
    });
  }

  onSchoolChange(schoolId: number | null): void {
    this.historySchoolId = schoolId;
    this.loadHistory();
  }

  formatStatus(status: string): string {
    if (!status) return '—';
    return status.charAt(0) + status.slice(1).toLowerCase();
  }

  statusTone(status: string): 'ok' | 'warn' | 'danger' {
    switch (status) {
      case 'APPROVED':  return 'ok';
      case 'PENDING':   return 'warn';
      case 'REJECTED':  return 'danger';
      case 'CANCELLED': return 'danger';
      default:          return 'ok';
    }
  }

  formatEnum(value: string): string {
    if (!value) return '—';
    return value.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }

  formatDate(value: string): string {
    if (!value) return '—';
    try {
      return new Date(value).toLocaleDateString('en-GB', {
        day: 'numeric',
        month: 'short',
        year: 'numeric',
        timeZone: 'UTC'
      });
    } catch {
      return value;
    }
  }
}
