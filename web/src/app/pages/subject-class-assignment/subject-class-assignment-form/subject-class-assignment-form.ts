import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BackendService } from '../../../util/backend.service';
import { SubjectClassAssignment, SubjectClassAssignmentPayload } from '../subject-class-assignment';
import { AcademicYear } from '../../academic-years/academic-year';
import {Subject} from '../../subject/subject';

@Component({
  selector: 'app-subject-class-assignment-form',
  standalone: false,
  templateUrl: './subject-class-assignment-form.html',
  styleUrl: './subject-class-assignment-form.scss'
})
export class SubjectClassAssignmentForm implements OnInit, OnChanges {

  @Input() visible = false;
  @Input() schoolClassId!: number;
  @Input() editingAssignment: SubjectClassAssignment | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() assignmentCreated = new EventEmitter<SubjectClassAssignmentPayload>();
  @Output() assignmentUpdated = new EventEmitter<{ id: number; payload: SubjectClassAssignmentPayload }>();

  assignmentForm: FormGroup;
  academicYears: AcademicYear[] = [];
  subjects: Subject[] = [];
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
      teacherId: [null]
    });
  }

  get isEditMode(): boolean {
    return this.editingAssignment !== null;
  }

  ngOnInit(): void {
    this.backend.get<AcademicYear[]>('/academic-years').subscribe({
      next: (years) => this.academicYears = years,
      error: () => this.loadError = 'Failed to load academic years.'
    });

    this.backend.get<Subject[]>('/subjects').subscribe({
      next: (subjects) => this.subjects = subjects,
      error: () => this.loadError = 'Failed to load subjects.'
    });

    this.backend.get<{
      id: number;
      firstName: string;
      lastName: string;
    }[]>('/users/teachers').subscribe({
      next: (teachers) => this.teachers = teachers,
      error: () => this.loadError = 'Failed to load teachers.'
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['editingAssignment']) {
      if (this.editingAssignment) {
        this.assignmentForm.patchValue({
          subjectId: this.editingAssignment.subjectId,
          academicYearId: this.editingAssignment.academicYearId,
          teacherId: this.editingAssignment.teacherId ?? null
        });
      } else {
        this.resetForm();
      }
    }
  }

  onSubmit(): void {
    if (this.assignmentForm.invalid) {
      this.assignmentForm.markAllAsTouched();
      return;
    }

    const formValue = this.assignmentForm.value;

    const payload: SubjectClassAssignmentPayload = {
      academicYearId: formValue.academicYearId,
      subjectId: formValue.subjectId,
      schoolClassId: this.schoolClassId,
      teacherId: formValue.teacherId || null
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
      teacherId: null
    });
  }
}
