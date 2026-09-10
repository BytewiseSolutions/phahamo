import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { SchoolClass, SchoolClassPayload } from '../class';

@Component({
  selector: 'app-class-list',
  standalone: false,
  templateUrl: './class-list.html',
  styleUrl: './class-list.scss'
})
export class ClassList implements OnInit {

  classes: SchoolClass[] = [];
  loading = false;
  showCreateForm = false;
  editingClass: SchoolClass | null = null;
  errorMessage = '';
  successMessage = '';
  selectedClass: SchoolClass | null = null;
  showView = false;

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.loadClasses();
  }

  loadClasses(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<SchoolClass[]>('/classes').subscribe({
      next: (classes) => {
        this.classes = classes;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load classes.';
        this.loading = false;
      }
    });
  }

  toggleCreateForm(): void {
    this.editingClass = null;
    this.showCreateForm = !this.showCreateForm;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onEdit(schoolClass: SchoolClass): void {
    this.editingClass = schoolClass;
    this.showCreateForm = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onFormClosed(): void {
    this.showCreateForm = false;
    this.editingClass = null;
  }

  onCreateClass(newClass: SchoolClassPayload): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.backend.post<SchoolClass>('/classes', newClass).subscribe({
      next: () => {
        this.successMessage = 'Class created.';
        this.showCreateForm = false;
        this.loadClasses();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to create class.';
      }
    });
  }

  onUpdateClass(event: { id: number; payload: SchoolClassPayload }): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.backend.put<SchoolClass>(
      `/classes/${event.id}`,
      event.payload
    ).subscribe({
      next: () => {
        this.successMessage = 'Class updated.';
        this.showCreateForm = false;
        this.editingClass = null;
        this.loadClasses();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update class.';
      }
    });
  }

  onDelete(schoolClass: SchoolClass): void {
    if (!confirm(`Delete class "${schoolClass.name}"?`)) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.backend.delete<void>(`/classes/${schoolClass.id}`).subscribe({
      next: () => {
        this.successMessage = 'Class deleted.';
        this.loadClasses();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to delete class.';
      }
    });
  }

  onView(schoolClass: SchoolClass): void {
    this.selectedClass = schoolClass;
    this.showView = true;
  }

  onViewClosed(): void {
    this.showView = false;
    this.selectedClass = null;
  }
}
