import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Subject } from '../subject';

@Component({
  selector: 'app-subject-list',
  standalone: false,
  templateUrl: './subject-list.html',
  styleUrl: './subject-list.scss'
})
export class SubjectList implements OnInit {

  subjects: Subject[] = [];
  loading = false;

  showCreateForm = false;
  editingSubject: Subject | null = null;

  errorMessage = '';
  successMessage = '';

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.loadSubjects();
  }

  loadSubjects(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<Subject[]>('/subjects').subscribe({
      next: (subjects) => {
        this.subjects = subjects;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load subjects.';
        this.loading = false;
      }
    });
  }

  toggleCreateForm(): void {
    this.editingSubject = null;
    this.showCreateForm = !this.showCreateForm;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onEdit(subject: Subject): void {
    this.editingSubject = subject;
    this.showCreateForm = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onFormClosed(): void {
    this.showCreateForm = false;
    this.editingSubject = null;
  }

  onCreateSubject(newSubject: any): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.backend.post<Subject>('/subjects', newSubject).subscribe({
      next: () => {
        this.successMessage = 'Subject created.';
        this.showCreateForm = false;
        this.loadSubjects();
      },
      error: (err) => {
        this.errorMessage = err.status === 409
          ? (err.error?.message || 'A subject with this code already exists.')
          : 'Something went wrong. Please try again.';
      }
    });
  }

  onUpdateSubject(event: { id: number; payload: any }): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.backend.put<Subject>(
      `/subjects/${event.id}`,
      event.payload
    ).subscribe({
      next: () => {
        this.successMessage = 'Subject updated.';
        this.showCreateForm = false;
        this.editingSubject = null;
        this.loadSubjects();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update subject.';
      }
    });
  }

  onDelete(subject: Subject): void {
    if (!confirm(`Delete subject "${subject.name}"?`)) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.backend.delete<void>(`/subjects/${subject.id}`).subscribe({
      next: () => {
        this.successMessage = 'Subject deleted.';
        this.loadSubjects();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to delete subject.';
      }
    });
  }
}
