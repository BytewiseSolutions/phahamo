import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Examination, ExaminationPayload } from '../examination';
import { Term } from '../../terms/term';
import { SubjectClassAssignment } from '../../subject-class-assignment/subject-class-assignment';

@Component({
  selector: 'app-examination-form',
  standalone: false,
  templateUrl: './examination-form.html',
  styleUrl: './examination-form.scss'
})
export class ExaminationForm implements OnChanges {

  @Input() visible = false;
  @Input() editing: Examination | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() save = new EventEmitter<{ id: number | null; payload: ExaminationPayload }>();

  terms: Term[] = [];
  subjectClassOptions: SubjectClassAssignment[] = [];

  selectedTermId: number | null = null;
  name = '';
  startDate = '';
  endDate = '';
  selectedKeys = new Set<string>();

  loading = false;
  errorMessage = '';

  get isEditMode(): boolean {
    return this.editing !== null;
  }

  constructor(private backend: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible) {
      this.reset();
      this.loadDependencies();
    }
  }

  loadDependencies(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<Term[]>('/terms').subscribe({
      next: (terms) => {
        this.terms = terms;

        this.backend.get<SubjectClassAssignment[]>('/subject-class-assignments').subscribe({
          next: (assignments) => {
            this.subjectClassOptions = assignments;
            this.loading = false;
            this.applyEditValues();
          },
          error: () => {
            this.errorMessage = 'Failed to load subject-class list.';
            this.loading = false;
          }
        });
      },
      error: () => {
        this.errorMessage = 'Failed to load terms.';
        this.loading = false;
      }
    });
  }

  applyEditValues(): void {
    if (!this.editing) return;

    this.selectedTermId = this.editing.termId;
    this.name = this.editing.name;
    this.startDate = this.editing.startDate;
    this.endDate = this.editing.endDate;

    this.selectedKeys = new Set(
      this.editing.subjectClasses.map(sc => this.keyFor(sc.subjectId, sc.classId))
    );
  }

  keyFor(subjectId: number, classId: number): string {
    return `${subjectId}-${classId}`;
  }

  isSelected(a: SubjectClassAssignment): boolean {
    return this.selectedKeys.has(this.keyFor(a.subjectId, a.schoolClassId));
  }

  toggle(a: SubjectClassAssignment): void {
    const key = this.keyFor(a.subjectId, a.schoolClassId);
    if (this.selectedKeys.has(key)) {
      this.selectedKeys.delete(key);
    } else {
      this.selectedKeys.add(key);
    }
  }

  formatAssignment(a: SubjectClassAssignment): string {
    return `${a.subjectName} — ${a.schoolClassName}`;
  }

  onSubmit(): void {
    if (this.selectedTermId == null) {
      this.errorMessage = 'Please select a term.';
      return;
    }
    if (!this.name.trim()) {
      this.errorMessage = 'Name is required.';
      return;
    }
    if (!this.startDate || !this.endDate) {
      this.errorMessage = 'Start and end dates are required.';
      return;
    }
    if (this.endDate < this.startDate) {
      this.errorMessage = 'End date must be on or after start date.';
      return;
    }
    if (this.selectedKeys.size === 0) {
      this.errorMessage = 'Select at least one subject and class.';
      return;
    }

    const subjectClasses = Array.from(this.selectedKeys).map(key => {
      const [subjectId, classId] = key.split('-').map(Number);
      return { subjectId, classId };
    });

    this.save.emit({
      id: this.editing?.id ?? null,
      payload: {
        termId: this.selectedTermId,
        name: this.name.trim(),
        startDate: this.startDate,
        endDate: this.endDate,
        subjectClasses
      }
    });
  }

  onClose(): void {
    this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) this.onClose();
  }

  private reset(): void {
    this.selectedTermId = null;
    this.name = '';
    this.startDate = '';
    this.endDate = '';
    this.selectedKeys = new Set();
    this.errorMessage = '';
  }
}
