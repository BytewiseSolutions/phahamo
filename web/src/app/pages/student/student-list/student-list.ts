import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Student, StudentPayload } from '../student';

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

  constructor(private backend: BackendService) {}

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
      next: () => {
        this.successMessage = 'Student registered.';
        this.showCreateForm = false;
        this.loadStudents();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to register student.';
      }
    });
  }

  onUpdateStudent(event: { id: number; payload: StudentPayload }): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.backend.put<Student>(
      `/students/${event.id}`,
      event.payload
    ).subscribe({
      next: () => {
        this.successMessage = 'Student updated.';
        this.showCreateForm = false;
        this.editingStudent = null;
        this.loadStudents();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update student.';
      }
    });
  }

  onDelete(student: Student): void {
    if (!confirm(`Delete student "${student.firstName} ${student.lastName}"?`)) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.backend.delete<void>(`/students/${student.id}`).subscribe({
      next: () => {
        this.successMessage = 'Student deleted.';
        this.loadStudents();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to delete student.';
      }
    });
  }
}
