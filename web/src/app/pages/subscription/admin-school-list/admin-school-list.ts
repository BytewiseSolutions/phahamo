import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import { SystemAdminSchool } from '../system-admin-school';

@Component({
  selector: 'app-admin-school-list',
  standalone: false,
  templateUrl: './admin-school-list.html',
  styleUrl: './admin-school-list.scss'
})
export class AdminSchoolList implements OnInit {

  schools: SystemAdminSchool[] = [];
  loading = false;
  errorMessage = '';

  searchTerm = '';
  statusFilter = 'ALL';
  dashboardFilter = '';

  showExtendDialog = false;
  showActivateDialog = false;
  selectedSchool: SystemAdminSchool | null = null;

  statusOptions = ['ALL', 'TRIAL', 'ACTIVE', 'EXPIRED', 'SUSPENDED', 'CANCELLED'];

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.dashboardFilter = params.get('filter') || '';
      this.statusFilter = params.get('status') || 'ALL';
      this.loadSchools();
    });
  }

  get filteredSchools(): SystemAdminSchool[] {
    return this.schools;
  }

  loadSchools(): void {
    this.loading = true;
    this.errorMessage = '';

    const params = new URLSearchParams();
    if (this.searchTerm.trim()) params.append('search', this.searchTerm.trim());
    if (this.statusFilter !== 'ALL') params.append('status', this.statusFilter);
    if (this.dashboardFilter) params.append('filter', this.dashboardFilter);
    const query = params.toString();

    this.backend.get<SystemAdminSchool[]>(
      `/admin/schools${query ? '?' + query : ''}`
    ).subscribe({
      next: (data) => {
        this.schools = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load schools.';
        this.loading = false;
      }
    });
  }

  onFiltersChanged(): void {
    this.loadSchools();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.statusFilter = 'ALL';
    this.dashboardFilter = '';
    this.loadSchools();
  }

  get hasFilters(): boolean {
    return this.searchTerm.trim().length > 0 || this.statusFilter !== 'ALL' || !!this.dashboardFilter;
  }

  get filterDescription(): string {
    if (this.dashboardFilter === 'TRIAL_ENDING') return 'Trials ending in the next 14 days';
    if (this.dashboardFilter === 'NEW_THIS_MONTH') return 'New schools registered this month';
    if (this.statusFilter === 'TRIAL') return 'Schools on trial';
    if (this.statusFilter === 'ACTIVE') return 'Schools with an active subscription';
    return '';
  }

  openExtendDialog(school: SystemAdminSchool): void {
    this.selectedSchool = school;
    this.showExtendDialog = true;
  }

  openActivateDialog(school: SystemAdminSchool): void {
    this.selectedSchool = school;
    this.showActivateDialog = true;
  }

  onDialogClosed(): void {
    this.showExtendDialog = false;
    this.showActivateDialog = false;
    this.selectedSchool = null;
  }

  onActionCompleted(): void {
    this.onDialogClosed();
    this.loadSchools();
  }

  async onSuspend(school: SystemAdminSchool): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Suspend School',
      message: `Suspend "${school.schoolName}"?\n\nAll users except the school admin will be blocked from accessing the system.`,
      confirmText: 'Suspend',
      danger: true
    });

    if (!confirmed) return;

    this.backend.post(
      `/admin/schools/${school.schoolId}/suspend`, {}
    ).subscribe({
      next: () => this.loadSchools(),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to suspend school.';
      }
    });
  }

  statusBadgeClass(status: string): string {
    switch (status) {
      case 'ACTIVE': return 'badge-active';
      case 'TRIAL': return 'badge-trial';
      case 'EXPIRED': return 'badge-expired';
      case 'SUSPENDED': return 'badge-suspended';
      case 'CANCELLED': return 'badge-cancelled';
      default: return '';
    }
  }

  formatDate(value: string | null): string {
    if (!value) return 'Lifetime';
    try {
      return new Date(value).toLocaleDateString('en-GB', {
        day: 'numeric', month: 'short', year: 'numeric', timeZone: 'UTC'
      });
    } catch {
      return value;
    }
  }
}
