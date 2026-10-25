import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import { Holiday, HolidayPayload } from '../holiday';

@Component({
  selector: 'app-holiday-list',
  standalone: false,
  templateUrl: './holiday-list.html',
  styleUrl: './holiday-list.scss'
})
export class HolidayList implements OnInit {
  holidays: Holiday[] = [];
  loading = false;
  saving = false;
  showForm = false;
  editing: Holiday | null = null;
  errorMessage = '';
  successMessage = '';

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService
  ) {}

  ngOnInit(): void {
    this.loadHolidays();
  }

  loadHolidays(): void {
    this.loading = true;
    this.errorMessage = '';
    this.backend.get<Holiday[]>('/holidays').subscribe({
      next: (holidays) => {
        this.holidays = holidays;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load holidays.';
        this.loading = false;
      }
    });
  }

  openCreate(): void {
    this.editing = null;
    this.showForm = true;
    this.clearMessages();
  }

  openEdit(holiday: Holiday): void {
    this.editing = holiday;
    this.showForm = true;
    this.clearMessages();
  }

  closeForm(): void {
    this.showForm = false;
    this.editing = null;
  }

  save(payload: HolidayPayload): void {
    this.clearMessages();
    if (!payload.name) {
      this.errorMessage = 'Holiday name is required.';
      return;
    }
    if (!payload.startDate || !payload.endDate) {
      this.errorMessage = 'Start date and end date are required.';
      return;
    }
    if (payload.endDate < payload.startDate) {
      this.errorMessage = 'End date must be on or after start date.';
      return;
    }

    this.saving = true;

    const request = this.editing
      ? this.backend.put<Holiday>(`/holidays/${this.editing.id}`, payload)
      : this.backend.post<Holiday>('/holidays', payload);

    request.subscribe({
      next: () => {
        this.successMessage = this.editing ? 'Holiday updated.' : 'Holiday added.';
        this.saving = false;
        this.closeForm();
        this.loadHolidays();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to save holiday.';
        this.saving = false;
      }
    });
  }

  async remove(holiday: Holiday): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Remove Holiday',
      message: `Remove "${holiday.name}"? Existing cancelled lessons will remain cancelled.`,
      confirmText: 'Remove',
      danger: true
    });
    if (!confirmed) return;

    this.backend.delete<void>(`/holidays/${holiday.id}`).subscribe({
      next: () => {
        this.successMessage = 'Holiday removed.';
        this.loadHolidays();
      },
      error: (err) => this.errorMessage = err.error?.message || 'Failed to remove holiday.'
    });
  }

  formatDate(value: string): string {
    return new Date(`${value}T00:00:00`).toLocaleDateString('en-GB', {
      day: 'numeric', month: 'short', year: 'numeric'
    });
  }

  private clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

}
