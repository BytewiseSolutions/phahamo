import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BackendService } from '../../../util/backend.service';
import { SubjectClassAssignment, SubjectClassAssignmentPayload } from '../subject-class-assignment';
import { AcademicYear } from '../../academic-years/academic-year';
import { Subject } from '../../subject/subject';

@Component({
  selector: 'app-subject-class-assignment-form',
  standalone: false,
  templateUrl: './subject-class-assignment-form.html',
  styleUrl: './subject-class-assignment-form.scss'
})
export class SubjectClassAssignmentForm implements OnInit, OnChanges {

  @Input() visible = false;
  @Input() schoolClassId: number | null = null;
  @Input() fixedTeacherId: number | null = null;
  @Input() editingAssignment: SubjectClassAssignment | null = null;
  @Input() assignedSubjectIds: number[] = [];

  @Output() closed = new EventEmitter<void>();
  @Output() assignmentCreated = new EventEmitter<SubjectClassAssignmentPayload>();
  @Output() assignmentUpdated = new EventEmitter<{ id: number; payload: SubjectClassAssignmentPayload }>();

  assignmentForm: FormGroup;
  academicYears: AcademicYear[] = [];
  subjects: Subject[] = [];
  classes: { id: number; name: string }[] = [];
  teachers: {
    id: number;
    firstName: string;
    lastName: string;
  }[] = [];
  loadError = '';

  constructor(private fb: FormBuilder, private backend: BackendService) {
    this.assignmentForm = this.fb.group({
      subjectId: [null, Validators.required],
      academicYearId: [null, Validators.required],
      schoolClassId: [null],
      teacherId: [null]
    });
  }

  get isEditMode(): boolean {
    return this.editingAssignment !== null;
  }

  get isClassLocked(): boolean {
    return this.schoolClassId !== null && this.schoolClassId !== undefined;
  }

  get isTeacherLocked(): boolean {
    return this.fixedTeacherId !== null && this.fixedTeacherId !== undefined;
  }

  get availableSubjects(): Subject[] {
    const currentSubjectId = this.editingAssignment?.subjectId;

    return this.subjects.filter(subject =>
      subject.id === currentSubjectId || !this.assignedSubjectIds.includes(subject.id)
    );
  }

  ngOnInit(): void {
    this.backend.get<AcademicYear[]>('/academic-years').subscribe({
      next: (years) => {
        this.academicYears = years;
        this.setDefaultAcademicYear();
      },
      error: () => this.loadError = 'Failed to load academic years.'
    });

    this.backend.get<Subject[]>('/subjects').subscribe({
      next: (subjects) => this.subjects = subjects,
      error: () => this.loadError = 'Failed to load subjects.'
    });

    if (!this.isClassLocked) {
      this.backend.get<{ id: number; name: string }[]>('/classes').subscribe({
        next: (classes) => this.classes = classes,
        error: () => this.loadError = 'Failed to load classes.'
      });
    }

    if (!this.isTeacherLocked) {
      this.backend.get<{
        id: number;
        firstName: string;
        lastName: string;
      }[]>('/users/teachers').subscribe({
        next: (teachers) => this.teachers = teachers,
        error: () => this.loadError = 'Failed to load teachers.'
      });
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible']?.currentValue && !this.editingAssignment) {
      this.resetForm();
      this.setDefaultAcademicYear();
    }

    if (changes['schoolClassId'] && this.isClassLocked) {
      this.assignmentForm.get('schoolClassId')?.setValue(this.schoolClassId);
    }

    if (changes['fixedTeacherId'] && this.isTeacherLocked) {
      this.assignmentForm.get('teacherId')?.setValue(this.fixedTeacherId);
    }

    if (changes['editingAssignment']) {
      if (this.editingAssignment) {
        this.assignmentForm.patchValue({
          subjectId: this.editingAssignment.subjectId,
          academicYearId: this.editingAssignment.academicYearId,
          schoolClassId: this.editingAssignment.schoolClassId,
          teacherId: this.editingAssignment.teacherId ?? null
        });
      } else {
        this.resetForm();
        this.setDefaultAcademicYear();
      }
    }
  }

  private setDefaultAcademicYear(): void {
    if (this.isEditMode || this.assignmentForm.get('academicYearId')?.value) {
      return;
    }

    const academicYear = this.academicYears.find(year => year.active)
      ?? (this.academicYears.length === 1 ? this.academicYears[0] : null);

    if (academicYear) {
      this.assignmentForm.get('academicYearId')?.setValue(academicYear.id);
    }
  }

  onSubmit(): void {
    if (this.assignmentForm.invalid) {
      this.assignmentForm.markAllAsTouched();
      return;
    }

    const formValue = this.assignmentForm.value;

    const resolvedClassId = this.isClassLocked
      ? this.schoolClassId!
      : formValue.schoolClassId;

    const resolvedTeacherId = this.isTeacherLocked
      ? this.fixedTeacherId!
      : (formValue.teacherId || null);

    const payload: SubjectClassAssignmentPayload = {
      academicYearId: formValue.academicYearId,
      subjectId: formValue.subjectId,
      schoolClassId: resolvedClassId,
      teacherId: resolvedTeacherId
    };

    if (this.isEditMode && this.editingAssignment) {
      this.assignmentUpdated.emit({
        id: this.editingAssignment.id,
        payload
      });
    } else {
      this.assignmentCreated.emit(payload);
    }

    this.resetForm();
  }

  onClose(): void {
    this.resetForm();
    this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }

  private resetForm(): void {
    this.assignmentForm.reset({
      subjectId: null,
      academicYearId: null,
      schoolClassId: this.isClassLocked ? this.schoolClassId : null,
      teacherId: this.isTeacherLocked ? this.fixedTeacherId : null
    });
  }
}
