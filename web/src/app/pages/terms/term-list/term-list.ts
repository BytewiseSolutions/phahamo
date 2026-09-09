import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Term, TermPayload } from '../term';

@Component({
  selector: 'app-term-list',
  standalone: false,
  templateUrl: './term-list.html',
  styleUrl: './term-list.scss'
})
export class TermList implements OnChanges {

  @Input() academicYearId!: number;

  terms: Term[] = [];
  loading = false;
  showForm = false;
  editingTerm: Term | null = null;
  errorMessage = '';
  successMessage = '';

  constructor(private backend: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['academicYearId'] && this.academicYearId) {
      this.loadTerms();
    }
  }

  loadTerms(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<Term[]>(
      `/terms/academic-year/${this.academicYearId}`
    ).subscribe({
      next: (terms) => {
        this.terms = terms;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load terms.';
        this.loading = false;
      }
    });
  }

  toggleCreateForm(): void {
    this.editingTerm = null;
    this.showForm = !this.showForm;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onEdit(term: Term): void {
    this.editingTerm = term;
    this.showForm = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onFormClosed(): void {
    this.showForm = false;
    this.editingTerm = null;
  }

  onCreateTerm(payload: TermPayload): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.backend.post<Term>('/terms', payload).subscribe({
      next: () => {
        this.successMessage = 'Term created.';
        this.showForm = false;
        this.loadTerms();
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || 'Failed to create term.';
      }
    });
  }

  onUpdateTerm(event: { id: number; payload: TermPayload }): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.backend.put<Term>(
      `/terms/${event.id}`,
      event.payload
    ).subscribe({
      next: () => {
        this.successMessage = 'Term updated.';
        this.showForm = false;
        this.editingTerm = null;
        this.loadTerms();
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || 'Failed to update term.';
      }
    });
  }

  onDelete(term: Term): void {
    if (!confirm(`Delete term "${term.name}"?`)) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.backend.delete<void>(`/terms/${term.id}`).subscribe({
      next: () => {
        this.successMessage = 'Term deleted.';
        this.loadTerms();
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || 'Failed to delete term.';
      }
    });
  }
}
