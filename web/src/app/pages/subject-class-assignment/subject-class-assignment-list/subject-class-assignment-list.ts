import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { SubjectClassAssignment, SubjectClassAssignmentPayload } from '../subject-class-assignment';
import {ConfirmDialogService} from '../../../util/confirm-dialog.service';

@Component({
  selector: 'app-subject-class-assignment-list',
  standalone: false,
  templateUrl: './subject-class-assignment-list.html',
  styleUrl: './subject-class-assignment-list.scss'
})
export class SubjectClassAssignmentList implements OnChanges {

  @Input() schoolClassId!: number;

  assignments: SubjectClassAssignment[] = [];
  loading = false;
  showForm = false;
  editingAssignment: SubjectClassAssignment | null = null;
  errorMessage = '';
  successMessage = '';

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['schoolClassId'] && this.schoolClassId) {
      this.loadAssignments();
    }
  }

  loadAssignments(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<SubjectClassAssignment[]>(
      `/subject-class-assignments/class/${this.schoolClassId}`
    ).subscribe({
      next: (assignments) => {
        this.assignments = assignments;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load subjects.';
        this.loading = false;
      }
    });
  }

  toggleCreateForm(): void {
    this.editingAssignment = null;
    this.showForm = !this.showForm;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onEdit(assignment: SubjectClassAssignment): void {
    this.editingAssignment = assignment;
    this.showForm = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onFormClosed(): void {
    this.showForm = false;
    this.editingAssignment = null;
  }

  onCreateAssignment(payload: SubjectClassAssignmentPayload): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.backend.post<SubjectClassAssignment>('/subject-class-assignments', payload).subscribe({
      next: () => {
        this.successMessage = 'Subject assigned to class.';
        this.showForm = false;
        this.loadAssignments();
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || 'Failed to assign subject.';
      }
    });
  }

  onUpdateAssignment(event: { id: number; payload: SubjectClassAssignmentPayload }): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.backend.put<SubjectClassAssignment>(
      `/subject-class-assignments/${event.id}`,
      event.payload
    ).subscribe({
      next: () => {
        this.successMessage = 'Assignment updated.';
        this.showForm = false;
        this.editingAssignment = null;
        this.loadAssignments();
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || 'Failed to update assignment.';
      }
    });
  }

  async onDelete(assignment: SubjectClassAssignment): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Delete Subject',
      message: `Delete "${assignment.subjectName}"? This action cannot be undone.`,
      confirmText: 'Delete',
      danger: true
    });

    if (!confirmed) return;

    this.errorMessage = '';
    this.successMessage = '';

    this.backend.delete<void>(`/subject-class-assignments/${assignment.id}`).subscribe({
      next: () => {
        this.successMessage = 'Subject removed from class.';
        this.loadAssignments();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to remove subject.';
      }
    });
  }
}
