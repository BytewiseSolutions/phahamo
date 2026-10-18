import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { AuthService } from '../../../util/auth.service';
import { SchoolClass } from '../../class/class';
import { Term } from '../../terms/term';
import { Remark } from '../remark';

@Component({
  selector: 'app-remarks-page',
  standalone: false,
  templateUrl: './remarks-page.html',
  styleUrl: './remarks-page.scss'
})
export class RemarksPage implements OnInit {
  classes: SchoolClass[] = [];
  terms: Term[] = [];
  selectedClassId: number | null = null;
  selectedTermId: number | null = null;

  students: Remark[] = [];
  loading = false;
  errorMessage = '';

  isTeacher = false;
  isPrincipal = false;
  isAdmin = false;

  editing: Record<number, { teacher: string, principal: string }> = {};

  constructor(private backend: BackendService, private authService: AuthService) {}

  ngOnInit(): void {
    this.isTeacher = this.authService.hasRole('TEACHER');
    this.isPrincipal = this.authService.hasRole('PRINCIPAL');
    this.isAdmin = this.authService.hasRole('SCHOOL_ADMIN') || this.authService.hasRole('SYSTEM_ADMIN');

    this.loadFilters();
  }

  loadFilters(): void {
    const classesUrl = this.isTeacher ? '/classes/my-classes' : '/classes';
    this.backend.get<SchoolClass[]>(classesUrl).subscribe({
      next: (classes) => {
        this.classes = classes;
        this.selectedClassId = classes[0]?.id ?? null;

        this.backend.get<Term[]>('/terms').subscribe({
          next: (terms) => {
            this.terms = terms;
            this.selectedTermId = terms[0]?.id ?? null;
            this.load();
          }
        });
      }
    });
  }

  load(): void {
    if (!this.selectedClassId || !this.selectedTermId) return;

    this.loading = true;
    this.errorMessage = '';

    this.backend.get<Remark[]>(
      `/remarks?classId=${this.selectedClassId}&termId=${this.selectedTermId}`
    ).subscribe({
      next: (res) => {
        this.students = res;
        this.editing = {};
        for (const s of res) {
          this.editing[s.studentId] = {
            teacher: s.classTeacherRemark || '',
            principal: s.principalRemark || ''
          };
        }
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load remarks';
        this.students = [];
        this.loading = false;
      }
    });
  }

  saveClassTeacherRemark(studentId: number): void {
    const remark = this.editing[studentId]?.teacher ?? '';
    this.backend.postSilently<void>(
      `/remarks/class-teacher/${studentId}?termId=${this.selectedTermId}`,
      { remark }
    ).subscribe({
      next: () => this.load(),
      error: (err) => this.errorMessage = err.error?.message || 'Failed to save'
    });
  }

  savePrincipalRemark(studentId: number): void {
    const remark = this.editing[studentId]?.principal ?? '';
    this.backend.postSilently<void>(
      `/remarks/principal/${studentId}?termId=${this.selectedTermId}`,
      { remark }
    ).subscribe({
      next: () => this.load(),
      error: (err) => this.errorMessage = err.error?.message || 'Failed to save'
    });
  }
}
