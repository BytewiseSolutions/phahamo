import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Student, StudentPayload } from '../student';
import { StudentStatus } from '../student-status';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import {SuccessDialogService} from '../../../util/success-dialog.service';

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
  successMessage = '';
  activePopoverId: number | null = null;
  popoverStatus: StudentStatus | '' = '';
  popoverReason = '';
  statusOptions = Object.values(StudentStatus);

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService,
    private successDialog: SuccessDialogService
  ) {}

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<Student[]>('/students').subscribe({
      next: (students) => {
        this.students = students;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load students.';
        this.loading = false;
      }
    });
  }

  toggleCreateForm(): void {
    this.editingStudent = null;
    this.showCreateForm = !this.showCreateForm;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onEdit(student: Student): void {
    this.editingStudent = student;
    this.showCreateForm = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onFormClosed(): void {
    this.showCreateForm = false;
    this.editingStudent = null;
  }

  onCreateStudent(payload: StudentPayload): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.backend.post<Student>('/students', payload).subscribe({
      next: async (student) => {
        this.showCreateForm = false;

        this.loadStudents();

        await this.successDialog.show({
          title: 'Student Registered',
          message: `Student "${student.firstName} ${student.lastName}" has been registered successfully.`
        });
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || 'Failed to register student.';
      }
    });
  }

  onUpdateStudent(event: {
    id: number;
    payload: StudentPayload
  }): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.backend.put<Student>(
      `/students/${event.id}`,
      event.payload
    ).subscribe({
      next: async (student) => {
        this.showCreateForm = false;
        this.editingStudent = null;
        this.loadStudents();

        await this.successDialog.show({
          title: 'Student Updated',
          message: `Student "${student.firstName} ${student.lastName}" has been updated successfully.`
        });
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || 'Failed to update student.';
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
    if (!this.popoverStatus || !this.popoverReason.trim()) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.backend.put<Student>(
      `/students/${student.id}/status`,
      {
        status: this.popoverStatus,
        reason: this.popoverReason.trim()
      }
    ).subscribe({
      next: () => {
        this.successMessage = 'Student status updated.';
        this.activePopoverId = null;
        this.loadStudents();
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || 'Failed to update status.';
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
      message: `Delete "${student.firstName} ${student.lastName}"? This action cannot be undone.`,
      confirmText: 'Delete',
      danger: true
    });

    if (!confirmed) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.backend.delete<void>(
      `/students/${student.id}`
    ).subscribe({
      next: () => {
        this.successMessage = 'Student deleted.';
        this.loadStudents();
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || 'Failed to delete student.';
      }
    });
  }
}
