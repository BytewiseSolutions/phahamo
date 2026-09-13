import { Component, OnInit } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { BackendService } from '../../../util/backend.service';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import { SuccessDialogService } from '../../../util/success-dialog.service';
import { Page } from '../../../util/page';
import { Student, StudentPayload } from '../student';
import { StudentStatus } from '../student-status';

@Component({
  selector: 'app-student-list',
  standalone: false,
  templateUrl: './student-list.html',
  styleUrl: './student-list.scss'
})
export class StudentList implements OnInit {

  students: Student[] = [];
  loading = false;
  showCreateForm = false;
  editingStudent: Student | null = null;
  errorMessage = '';
  activePopoverId: number | null = null;
  popoverStatus = '';
  popoverReason = '';
  statusOptions = Object.values(StudentStatus);

  searchTerm = '';
  private searchDebounce: ReturnType<typeof setTimeout> | null = null;

  page = 0;
  size = 20;
  pageSizeOptions = [20, 40, 60];
  totalElements = 0;
  totalPages = 0;

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService,
    private successDialog: SuccessDialogService
  ) {}

  ngOnInit(): void {
    this.loadStudents();
  }

  onSearchChange(): void {
    if (this.searchDebounce) {
      clearTimeout(this.searchDebounce);
    }
    this.searchDebounce = setTimeout(() => {
      this.page = 0;
      this.loadStudents();
    }, 300);
  }

  onPageSizeChange(): void {
    this.page = 0;
    this.loadStudents();
  }

  loadStudents(): void {
    this.loading = true;
    this.errorMessage = '';

    let params = new HttpParams()
      .set('page', this.page)
      .set('size', this.size);

    if (this.searchTerm.trim()) {
      params = params.set('search', this.searchTerm.trim());
    }

    this.backend.get<Page<Student>>('/students', params).subscribe({
      next: (result) => {
        this.students = result.content;
        this.totalElements = result.page.totalElements;
        this.totalPages = result.page.totalPages;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load students.';
        this.loading = false;
      }
    });
  }

  onPageChange(newPage: number): void {
    if (newPage < 0 || newPage >= this.totalPages) return;
    this.page = newPage;
    this.loadStudents();
  }

  toggleCreateForm(): void {
    this.editingStudent = null;
    this.showCreateForm = !this.showCreateForm;
    this.errorMessage = '';
  }

  onEdit(student: Student): void {
    this.editingStudent = student;
    this.showCreateForm = true;
    this.errorMessage = '';
  }

  onFormClosed(): void {
    this.showCreateForm = false;
    this.editingStudent = null;
  }

  onCreateStudent(payload: StudentPayload): void {
    this.errorMessage = '';

    this.backend.post<Student>('/students', payload).subscribe({
      next: () => {
        this.showCreateForm = false;
        this.loadStudents();
        this.successDialog.show({ message: 'Student registered.' });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to register student.';
      }
    });
  }

  onUpdateStudent(event: { id: number; payload: StudentPayload }): void {
    this.errorMessage = '';

    this.backend.put<Student>(
      `/students/${event.id}`,
      event.payload
    ).subscribe({
      next: () => {
        this.showCreateForm = false;
        this.editingStudent = null;
        this.loadStudents();
        this.successDialog.show({ message: 'Student updated.' });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update student.';
      }
    });
  }

  onStatusBadgeClick(student: Student): void {
    if (this.activePopoverId === student.id) {
      this.activePopoverId = null;
      return;
    }
    this.activePopoverId = student.id;
    this.popoverStatus = student.status;
    this.popoverReason = '';
  }

  onChangeStatus(student: Student): void {
    if (!this.popoverStatus || !this.popoverReason.trim()) return;

    this.errorMessage = '';

    this.backend.put<Student>(`/students/${student.id}/status`, {
      status: this.popoverStatus,
      reason: this.popoverReason.trim()
    }).subscribe({
      next: () => {
        this.activePopoverId = null;
        this.loadStudents();
        this.successDialog.show({ message: 'Student status updated.' });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update status.';
        this.activePopoverId = null;
      }
    });
  }

  onPopoverCancel(): void {
    this.activePopoverId = null;
    this.popoverReason = '';
  }

  async onDelete(student: Student): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Delete Student',
      message: `Delete student "${student.firstName} ${student.lastName}"?`,
      confirmText: 'Delete',
      danger: true
    });

    if (!confirmed) return;

    this.errorMessage = '';

    this.backend.delete<void>(`/students/${student.id}`).subscribe({
      next: () => {
        this.loadStudents();
        this.successDialog.show({ message: 'Student deleted.' });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to delete student.';
      }
    });
  }
}
