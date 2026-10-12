import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { AuthService } from '../../../util/auth.service';
import { TermResult } from '../term-result';
import { Term } from '../../terms/term';
import { Subject } from '../../subject/subject';
import { SchoolClass } from '../../class/class';
import {TermResultStatus} from '../term-result-status';

@Component({
  selector: 'app-class-results',
  standalone: false,
  templateUrl: './class-results.html',
  styleUrl: './class-results.scss'
})
export class ClassResults implements OnInit {

  classes: SchoolClass[] = [];
  subjects: Subject[] = [];
  terms: Term[] = [];

  selectedClassId: number | null = null;
  selectedSubjectId: number | null = null;
  selectedTermId: number | null = null;

  results: TermResult[] = [];

  loadingFilters = false;
  loadingResults = false;
  calculating = false;
  finalising = false;
  reopening = false;

  errorMessage = '';

  isAdmin = false;

  showReopenModal = false;
  reopenReason = '';

  showOverrideForm = false;
  overrideResult: TermResult | null = null;

  constructor(
    private backend: BackendService,
    private authService: AuthService,
    private route: ActivatedRoute
  ) {
    this.isAdmin = this.authService.hasRole('SCHOOL_ADMIN')
      || this.authService.hasRole('SYSTEM_ADMIN')
      || this.authService.hasRole('PRINCIPAL');
  }

  ngOnInit(): void {
    this.loadFilters();
  }

  loadFilters(): void {
    this.loadingFilters = true;
    this.errorMessage = '';

    this.backend.get<SchoolClass[]>('/classes').subscribe({
      next: (classes) => {
        this.classes = classes;

        this.backend.get<Subject[]>('/subjects').subscribe({
          next: (subjects) => {
            this.subjects = subjects;

            this.backend.get<Term[]>('/terms').subscribe({
              next: (terms) => {
                this.terms = terms;
                this.loadingFilters = false;

                const queryClassId = this.route.snapshot.queryParamMap.get('classId');
                const querySubjectId = this.route.snapshot.queryParamMap.get('subjectId');
                const queryTermId = this.route.snapshot.queryParamMap.get('termId');

                this.selectedClassId = queryClassId ? Number(queryClassId) : (classes[0]?.id ?? null);
                this.selectedSubjectId = querySubjectId ? Number(querySubjectId) : (subjects[0]?.id ?? null);
                this.selectedTermId = queryTermId ? Number(queryTermId) : (terms[0]?.id ?? null);

                if (this.selectedClassId && this.selectedSubjectId && this.selectedTermId) {
                  this.loadResults();
                }
              },
              error: () => {
                this.errorMessage = 'Failed to load terms.';
                this.loadingFilters = false;
              }
            });
          },
          error: () => {
            this.errorMessage = 'Failed to load subjects.';
            this.loadingFilters = false;
          }
        });
      },
      error: () => {
        this.errorMessage = 'Failed to load classes.';
        this.loadingFilters = false;
      }
    });
  }

  onFilterChange(): void {
    if (this.selectedClassId && this.selectedSubjectId && this.selectedTermId) {
      this.loadResults();
    }
  }

  loadResults(): void {
    this.loadingResults = true;
    this.errorMessage = '';

    const url = `/term-results?classId=${this.selectedClassId}&subjectId=${this.selectedSubjectId}&termId=${this.selectedTermId}`;

    this.backend.get<TermResult[]>(url).subscribe({
      next: (results) => {
        this.results = this.sortByName(results);
        this.loadingResults = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load results.';
        this.results = [];
        this.loadingResults = false;
      }
    });
  }

  private sortByName(list: TermResult[]): TermResult[] {
    return [...list].sort((a, b) => {
      const an = `${a.studentLastName} ${a.studentFirstName}`.toLowerCase();
      const bn = `${b.studentLastName} ${b.studentFirstName}`.toLowerCase();
      return an.localeCompare(bn);
    });
  }

  onCalculate(): void {
    if (!this.selectedClassId || !this.selectedSubjectId || !this.selectedTermId) return;

    this.calculating = true;
    this.errorMessage = '';

    const url = `/term-results/calculate?classId=${this.selectedClassId}&subjectId=${this.selectedSubjectId}&termId=${this.selectedTermId}`;

    this.backend.postSilently<TermResult[]>(url, {}).subscribe({
      next: (results) => {
        this.results = this.sortByName(results);
        this.calculating = false;
      },
      error: (err) => {
        this.calculating = false;
        this.errorMessage = err.error?.message || 'Failed to calculate results.';
      }
    });
  }

  onFinalise(): void {
    if (!this.selectedClassId || !this.selectedSubjectId || !this.selectedTermId) return;

    this.finalising = true;
    this.errorMessage = '';

    const url = `/term-results/finalise?classId=${this.selectedClassId}&subjectId=${this.selectedSubjectId}&termId=${this.selectedTermId}`;

    this.backend.postSilently<TermResult[]>(url, {}).subscribe({
      next: (results) => {
        this.results = this.sortByName(results);
        this.finalising = false;
      },
      error: (err) => {
        this.finalising = false;
        this.errorMessage = err.error?.message || 'Failed to finalise results.';
      }
    });
  }

  openReopenModal(): void {
    this.reopenReason = '';
    this.showReopenModal = true;
    this.errorMessage = '';
  }

  closeReopenModal(): void {
    this.showReopenModal = false;
    this.reopenReason = '';
  }

  submitReopen(): void {
    if (!this.reopenReason.trim()) {
      this.errorMessage = 'A reason is required to reopen results.';
      return;
    }
    if (!this.selectedClassId || !this.selectedSubjectId || !this.selectedTermId) return;

    this.reopening = true;
    this.errorMessage = '';

    const url = `/term-results/reopen?classId=${this.selectedClassId}&subjectId=${this.selectedSubjectId}&termId=${this.selectedTermId}&reason=${encodeURIComponent(this.reopenReason.trim())}`;

    this.backend.postSilently<TermResult[]>(url, {}).subscribe({
      next: (results) => {
        this.results = this.sortByName(results);
        this.reopening = false;
        this.closeReopenModal();
      },
      error: (err) => {
        this.reopening = false;
        this.errorMessage = err.error?.message || 'Failed to reopen results.';
      }
    });
  }

  openOverride(result: TermResult): void {
    this.overrideResult = result;
    this.showOverrideForm = true;
    this.errorMessage = '';
  }

  closeOverrideForm(): void {
    this.showOverrideForm = false;
    this.overrideResult = null;
  }

  onOverrideSaved(): void {
    this.closeOverrideForm();
    this.loadResults();
  }

  get anyFinalised(): boolean {
    return this.results.some(r => r.status === TermResultStatus.FINALISED);
  }

  get allFinalised(): boolean {
    return this.results.length > 0
      && this.results.every(r => r.status === TermResultStatus.FINALISED);
  }

  get canFinalise(): boolean {
    return this.results.length > 0 && !this.allFinalised;
  }

  statusLabel(status: TermResultStatus): string {
    return status === TermResultStatus.FINALISED ? 'Finalised' : 'Calculated';
  }
}
