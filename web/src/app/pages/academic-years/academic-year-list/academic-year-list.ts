import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import {AcademicYear, AcademicYearPayload} from '../academic-year';

@Component({
  selector: 'app-academic-year-list',
  standalone: false,
  templateUrl: './academic-year-list.html',
  styleUrl: './academic-year-list.scss'
})
export class AcademicYearList implements OnInit {

  academicYears: AcademicYear[] = [];
  loading = false;
  showCreateForm = false;
  editingAcademicYear: AcademicYear | null = null;
  errorMessage = '';
  successMessage = '';
  selectedAcademicYear: AcademicYear | null = null;
  showView = false;

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.loadAcademicYears();
  }

  loadAcademicYears(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<AcademicYear[]>('/academic-years').subscribe({
      next: (academicYears) => {
        this.academicYears = academicYears;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load academic years.';
        this.loading = false;
      }
    });
  }

  toggleCreateForm(): void {
    this.editingAcademicYear = null;
    this.showCreateForm = !this.showCreateForm;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onEdit(academicYear: AcademicYear): void {
    this.editingAcademicYear = academicYear;
    this.showCreateForm = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onFormClosed(): void {
    this.showCreateForm = false;
    this.editingAcademicYear = null;
  }

  onCreateAcademicYear(newAcademicYear: AcademicYearPayload): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.backend.post<AcademicYear>('/academic-years', newAcademicYear).subscribe({
      next: () => {
        this.successMessage = 'Academic year created.';
        this.showCreateForm = false;
        this.loadAcademicYears();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to create academic year.';
      }
    });
  }

  onUpdateAcademicYear(event: { id: number; payload: AcademicYearPayload }): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.backend.put<AcademicYear>(
      `/academic-years/${event.id}`,
      event.payload
    ).subscribe({
      next: () => {
        this.successMessage = 'Academic year updated.';
        this.showCreateForm = false;
        this.editingAcademicYear = null;
        this.loadAcademicYears();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update academic year.';
      }
    });
  }

  onDelete(academicYear: AcademicYear): void {
    if (!confirm(`Delete academic year "${academicYear.name}"?`)) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.backend.delete<void>(`/academic-years/${academicYear.id}`).subscribe({
      next: () => {
        this.successMessage = 'Academic year deleted.';
        this.loadAcademicYears();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to delete academic year.';
      }
    });
  }
  onView(academicYear: AcademicYear): void {
    this.selectedAcademicYear = academicYear;
    this.showView = true;
  }
  onViewClosed(): void {
    this.showView = false;
    this.selectedAcademicYear = null;
  }

}
