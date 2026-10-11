import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { Assessment } from '../assessment';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';

@Component({
  selector: 'app-grade-list',
  standalone: false,
  templateUrl: './grade-list.html',
  styleUrl: './grade-list.scss'
})
export class GradeList implements OnInit {

  assessments: Assessment[] = [];
  loading = false;
  errorMessage = '';

  showForm = false;
  editing: Assessment | null = null;
  saving = false;

  constructor(
    private backend: BackendService,
    private router: Router,
    private confirmDialog: ConfirmDialogService
  ) {}

  ngOnInit(): void {
    this.loadAssessments();
  }

  loadAssessments(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<Assessment[]>('/assessments').subscribe({
      next: (list) => {
        this.assessments = list;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load assessments.';
        this.loading = false;
      }
    });
  }

  openCreate(): void {
    this.editing = null;
    this.showForm = true;
  }

  openEdit(a: Assessment): void {
    this.editing = a;
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.editing = null;
    this.saving = false;
  }

  onSave(event: {
    id: number | null;
    assignmentId: number | null;
    termId: number;
    name: string;
    maxMark: number;
    weight: number;
    date: string;
  }): void {
    this.saving = true;
    this.errorMessage = '';

    if (event.id) {
      const payload = {
        termId: event.termId,
        name: event.name,
        maxMark: event.maxMark,
        weight: event.weight,
        date: event.date
      };
      this.backend.put<Assessment>(`/assessments/${event.id}`, payload).subscribe({
        next: () => {
          this.saving = false;
          this.closeForm();
          this.loadAssessments();
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = err.error?.message || 'Failed to update assessment.';
        }
      });
    } else {
      const payload = {
        assignmentId: event.assignmentId,
        termId: event.termId,
        name: event.name,
        maxMark: event.maxMark,
        weight: event.weight,
        date: event.date
      };
      this.backend.post<Assessment>('/assessments', payload).subscribe({
        next: () => {
          this.saving = false;
          this.closeForm();
          this.loadAssessments();
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = err.error?.message || 'Failed to create assessment.';
        }
      });
    }
  }

  openMarks(a: Assessment): void {
    this.router.navigate(['/grades', a.id, 'marks']);
  }

  async onDelete(a: Assessment): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Delete Assessment',
      message: `Delete "${a.name}" for ${a.subjectName} — ${a.className}?`,
      confirmText: 'Delete',
      danger: true
    });

    if (!confirmed) return;

    this.backend.delete<void>(`/assessments/${a.id}`).subscribe({
      next: () => this.loadAssessments(),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to delete assessment.';
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
}
