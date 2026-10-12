import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { AuthService } from '../../../util/auth.service';
import {Assessment, AssessmentType} from '../assessment';
import { SubjectClassAssignment } from '../../subject-class-assignment/subject-class-assignment';
import { Term } from '../../terms/term';
import {Examination} from '../../examination/examination';

@Component({
  selector: 'app-assessment-form',
  standalone: false,
  templateUrl: './assessment-form.html',
  styleUrl: './assessment-form.scss'
})
export class AssessmentForm implements OnChanges {

  @Input() visible = false;
  @Input() editing: Assessment | null = null;
  examinations: Examination[] = [];
  selectedType: AssessmentType = 'CONTINUOUS';
  selectedExaminationId: number | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() save = new EventEmitter<{
    id: number | null;
    assignmentId: number | null;
    type: AssessmentType | undefined;
    examinationId: number | null;
    termId: number;
    name: string;
    maxMark: number;
    weight: number;
    date: string;
  }>();
  assignments: SubjectClassAssignment[] = [];
  terms: Term[] = [];

  selectedAssignmentId: number | null = null;
  selectedTermId: number | null = null;
  name = '';
  maxMark = 100;
  weight = 20;
  date: string = new Date().toISOString().substring(0, 10);

  loading = false;
  errorMessage = '';

  isTeacher = false;

  get isEditMode(): boolean {
    return this.editing !== null;
  }

  constructor(private backend: BackendService, private authService: AuthService) {
    this.isTeacher = this.authService.hasRole('TEACHER');
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible) {
      this.reset();
      this.loadDependencies();
    }
  }
  onTypeChange(type: AssessmentType): void {
    this.selectedType = type;
    this.selectedExaminationId = null;
    if (type === 'EXAM') {
      this.loadExaminations();
    }
  }

  private loadExaminations(): void {
    if (!this.selectedTermId) {
      this.examinations = [];
      return;
    }

    this.backend.get<Examination[]>(`/examinations?termId=${this.selectedTermId}`).subscribe({
      next: (list) => this.examinations = list,
      error: () => {
        this.examinations = [];
        this.errorMessage = 'Failed to load examinations.';
      }
    });
  }

  loadDependencies(): void {
    this.loading = true;
    this.errorMessage = '';

    if (this.isEditMode && this.editing) {
      this.loadTermsForYear(this.editing.academicYearId);
      return;
    }

    if (!this.isTeacher) {
      this.errorMessage = 'Only teachers can create assessments.';
      this.loading = false;
      return;
    }

    this.backend.get<SubjectClassAssignment[]>('/subject-class-assignments/my-assignments').subscribe({
      next: (assignments) => {
        this.assignments = assignments;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load your assignments.';
        this.loading = false;
      }
    });
  }

  loadTermsForYear(academicYearId: number): void {
    this.backend.get<Term[]>(`/terms/academic-year/${academicYearId}`).subscribe({
      next: (terms) => {
        this.terms = terms;
        this.loading = false;
        this.applyEditValues();
      },
      error: () => {
        this.errorMessage = 'Failed to load terms.';
        this.loading = false;
      }
    });
  }

  onAssignmentChange(assignmentId: number | null): void {
    this.selectedAssignmentId = assignmentId;
    this.selectedTermId = null;
    this.terms = [];

    const assignment = this.assignments.find(a => a.id === assignmentId);
    if (!assignment) return;

    this.loadTermsForYear(assignment.academicYearId);
  }

  applyEditValues(): void {
    if (this.editing) {
      this.selectedAssignmentId = this.editing.assignmentId;
      this.selectedTermId = this.editing.termId;
      this.name = this.editing.name;
      this.maxMark = this.editing.maxMark;
      this.weight = this.editing.weight;
      this.date = this.editing.date?.substring(0, 10) ?? '';
    }
  }

  onSubmit(): void {
    if (!this.isEditMode && this.selectedAssignmentId == null) {
      this.errorMessage = 'Please select a subject and class.';
      return;
    }
    if (this.selectedTermId == null) {
      this.errorMessage = 'Please select a term.';
      return;
    }
    if (!this.name.trim()) {
      this.errorMessage = 'Name is required.';
      return;
    }
    if (this.maxMark <= 0) {
      this.errorMessage = 'Max mark must be greater than zero.';
      return;
    }
    if (this.weight < 0 || this.weight > 100) {
      this.errorMessage = 'Weight must be between 0 and 100.';
      return;
    }
    if (!this.date) {
      this.errorMessage = 'Date is required.';
      return;
    }

    if (this.selectedType === 'EXAM' && this.selectedExaminationId == null) {
      this.errorMessage = 'Please select an examination.';
      return;
    }

    this.save.emit({
      id: this.editing?.id ?? null,
      assignmentId: this.isEditMode ? null : this.selectedAssignmentId,
      type: this.isEditMode ? undefined : this.selectedType,
      examinationId: this.selectedType === 'EXAM' ? this.selectedExaminationId : null,
      termId: this.selectedTermId,
      name: this.name.trim(),
      maxMark: this.maxMark,
      weight: this.weight,
      date: this.date
    });
  }

  onClose(): void {
    this.closed.emit();
  }

  onTermChange(termId: number | null): void {
    this.selectedTermId = termId;
    this.selectedExaminationId = null;

    if (this.selectedType === 'EXAM' && termId != null) {
      this.loadExaminations();
    }
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) this.onClose();
  }

  formatAssignment(a: SubjectClassAssignment): string {
    return `${a.subjectName} — ${a.schoolClassName}`;
  }

  private reset(): void {
    this.selectedAssignmentId = null;
    this.selectedTermId = null;
    this.name = '';
    this.maxMark = 100;
    this.weight = 20;
    this.date = new Date().toISOString().substring(0, 10);
    this.errorMessage = '';
    this.terms = [];
    this.assignments = [];
  }
}
