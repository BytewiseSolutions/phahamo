import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import { SubjectClassAssignment, SubjectClassAssignmentPayload } from '../../subject-class-assignment/subject-class-assignment';

@Component({
  selector: 'app-teacher-subject-list',
  standalone: false,
  templateUrl: './teacher-subject-list.html',
  styleUrl: './teacher-subject-list.scss'
})
export class TeacherSubjectList implements OnChanges {

  @Input() teacherUserId!: number;

  assignments: SubjectClassAssignment[] = [];
  loading = false;
  showForm = false;
  editingAssignment: SubjectClassAssignment | null = null;
  errorMessage = '';

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['teacherUserId'] && this.teacherUserId) {
      this.loadAssignments();
    }
  }

  loadAssignments(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<SubjectClassAssignment[]>(
      `/subject-class-assignments/teacher/${this.teacherUserId}`
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
  }

  onEdit(assignment: SubjectClassAssignment): void {
    this.editingAssignment = assignment;
    this.showForm = true;
    this.errorMessage = '';
  }

  onFormClosed(): void {
    this.showForm = false;
    this.editingAssignment = null;
  }

  onCreateAssignment(payload: SubjectClassAssignmentPayload): void {
    this.errorMessage = '';

    this.backend.post<SubjectClassAssignment>('/subject-class-assignments', payload).subscribe({
      next: () => {
        this.showForm = false;
        this.loadAssignments();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to assign subject.';
      }
    });
  }

  onUpdateAssignment(event: { id: number; payload: SubjectClassAssignmentPayload }): void {
    this.errorMessage = '';

    this.backend.put<SubjectClassAssignment>(
      `/subject-class-assignments/${event.id}`,
      event.payload
    ).subscribe({
      next: () => {
        this.showForm = false;
        this.editingAssignment = null;
        this.loadAssignments();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update assignment.';
      }
    });
  }

  async onDelete(assignment: SubjectClassAssignment): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Delete Subject',
      message: `Remove "${assignment.subjectName}" from this teacher?`,
      confirmText: 'Delete',
      danger: true
    });

    if (!confirmed) return;

    this.errorMessage = '';

    this.backend.delete<void>(`/subject-class-assignments/${assignment.id}`).subscribe({
      next: () => {
        this.loadAssignments();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to remove subject.';
      }
    });
  }
}
