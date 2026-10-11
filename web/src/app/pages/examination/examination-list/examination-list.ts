import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Examination, ExaminationPayload } from '../examination';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';

@Component({
  selector: 'app-examination-list',
  standalone: false,
  templateUrl: './examination-list.html',
  styleUrl: './examination-list.scss'
})
export class ExaminationList implements OnInit {

  examinations: Examination[] = [];
  loading = false;
  errorMessage = '';

  showForm = false;
  editing: Examination | null = null;
  saving = false;

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService
  ) {}

  ngOnInit(): void {
    this.loadExaminations();
  }

  loadExaminations(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<Examination[]>('/examinations').subscribe({
      next: (list) => {
        this.examinations = list;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load examinations.';
        this.loading = false;
      }
    });
  }

  openCreate(): void {
    this.editing = null;
    this.showForm = true;
  }

  openEdit(e: Examination): void {
    this.editing = e;
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.editing = null;
    this.saving = false;
  }

  onSave(event: { id: number | null; payload: ExaminationPayload }): void {
    this.saving = true;
    this.errorMessage = '';

    if (event.id) {
      this.backend.put<Examination>(`/examinations/${event.id}`, event.payload).subscribe({
        next: () => {
          this.saving = false;
          this.closeForm();
          this.loadExaminations();
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = err.error?.message || 'Failed to update examination.';
        }
      });
    } else {
      this.backend.post<Examination>('/examinations', event.payload).subscribe({
        next: () => {
          this.saving = false;
          this.closeForm();
          this.loadExaminations();
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = err.error?.message || 'Failed to create examination.';
        }
      });
    }
  }

  async onDelete(e: Examination): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Delete Examination',
      message: `Delete "${e.name}"?`,
      confirmText: 'Delete',
      danger: true
    });

    if (!confirmed) return;

    this.backend.delete<void>(`/examinations/${e.id}`).subscribe({
      next: () => this.loadExaminations(),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to delete examination.';
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

  subjectClassSummary(e: Examination): string {
    if (e.subjectClasses.length <= 2) {
      return e.subjectClasses
        .map(sc => `${sc.subjectName} ${sc.className}`)
        .join(', ');
    }
    return `${e.subjectClasses.length} subject-class links`;
  }
}
