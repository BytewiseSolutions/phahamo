import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { AuthService } from '../../../util/auth.service';
import { AuditLog } from './audit-log';
import { AuditPage } from './audit-page';
import { AuditUser } from './audit-user';

@Component({
  selector: 'app-audit-log-page',
  standalone: false,
  templateUrl: './audit-log-page.html',
  styleUrl: './audit-log-page.scss'
})
export class AuditLogPage implements OnInit {
  logs: AuditLog[] = [];
  users: AuditUser[] = [];
  actionFilter = '';
  actorFilter = '';
  fromDate = '';
  toDate = '';
  page = 0;
  totalPages = 0;
  totalElements = 0;
  loading = false;
  errorMessage = '';

  readonly actions = [
    { value: 'LOGIN', label: 'Login' },
    { value: 'LOGOUT', label: 'Logout' },
    { value: 'FAILED_LOGIN', label: 'Failed login' },
    { value: 'HOLIDAY_CREATED', label: 'Holiday created' },
    { value: 'HOLIDAY_UPDATED', label: 'Holiday edited' },
    { value: 'HOLIDAY_DELETED', label: 'Holiday deleted' },
    { value: 'TIMETABLE_CREATED', label: 'Timetable entry created' },
    { value: 'TIMETABLE_UPDATED', label: 'Timetable entry edited' },
    { value: 'TIMETABLE_DELETED', label: 'Timetable entry deleted' },
    { value: 'TIMETABLE_PUBLISHED', label: 'Timetable published' },
    { value: 'ATTENDANCE_CHANGED', label: 'Attendance changed' },
    { value: 'FEE_STRUCTURE_CREATED', label: 'Fee structure created' },
    { value: 'FEE_STRUCTURE_UPDATED', label: 'Fee structure edited' },
    { value: 'FEE_STRUCTURE_DELETED', label: 'Fee structure deleted' },
    { value: 'ACADEMIC_YEAR_CREATED', label: 'Academic year created' },
    { value: 'ACADEMIC_YEAR_UPDATED', label: 'Academic year edited' },
    { value: 'ACADEMIC_YEAR_DELETED', label: 'Academic year deleted' },
    { value: 'TERM_CREATED', label: 'Term created' },
    { value: 'TERM_UPDATED', label: 'Term edited' },
    { value: 'TERM_DELETED', label: 'Term deleted' },
    { value: 'PAYMENT_RECORDED', label: 'Payment recorded' },
    { value: 'PAYMENT_REVERSED', label: 'Payment reversed' },
    { value: 'RESULT_CHANGED_AFTER_FINALISATION', label: 'Finalised result changed' },
    { value: 'STUDENT_STATUS_CHANGED', label: 'Student status changed' },
    { value: 'USER_CREATED', label: 'User created' },
    { value: 'USER_DEACTIVATED', label: 'User deactivated' },
    { value: 'USER_ROLE_CHANGED', label: 'User roles changed' }
  ];

  get canView(): boolean {
    return this.authService.hasRole('SCHOOL_ADMIN') || this.authService.hasRole('SYSTEM_ADMIN');
  }

  constructor(private backend: BackendService, private authService: AuthService) {}

  ngOnInit(): void {
    const today = new Date();
    const thirtyDaysAgo = new Date(today);
    thirtyDaysAgo.setDate(today.getDate() - 30);
    this.fromDate = this.toInputDate(thirtyDaysAgo);
    this.toDate = this.toInputDate(today);
    this.loadUsers();
    this.load();
  }

  private toInputDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  loadUsers(): void {
    this.backend.get<AuditUser[]>('/users').subscribe({
      next: users => this.users = users,
      error: () => this.users = []
    });
  }

  load(): void {
    this.loading = true;
    this.errorMessage = '';
    const params = new URLSearchParams({ page: String(this.page), size: '20' });
    if (this.actionFilter) params.set('action', this.actionFilter);
    if (this.actorFilter) params.set('actorId', this.actorFilter);
    if (this.fromDate) params.set('from', this.fromDate);
    if (this.toDate) params.set('to', this.toDate);

    this.backend.get<AuditPage>(`/audit-log?${params.toString()}`).subscribe({
      next: result => {
        const metadata = result.page ?? result;
        this.logs = result.content ?? [];
        this.page = metadata.number ?? 0;
        this.totalPages = metadata.totalPages ?? 0;
        this.totalElements = metadata.totalElements ?? 0;
        this.loading = false;
      },
      error: err => {
        this.errorMessage = err.error?.message || 'Failed to load audit log.';
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.page = 0;
    this.load();
  }

  clearFilters(): void {
    this.actionFilter = '';
    this.actorFilter = '';
    this.fromDate = '';
    this.toDate = '';
    this.applyFilters();
  }

  previousPage(): void {
    if (this.page > 0) { this.page--; this.load(); }
  }

  nextPage(): void {
    if (this.page + 1 < this.totalPages) { this.page++; this.load(); }
  }

  formatDate(value: string): string {
    return new Date(value).toLocaleString('en-GB', {
      day: 'numeric', month: 'short', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }

  actionLabel(action: string): string {
    return this.actions.find(item => item.value === action)?.label || action;
  }
}
