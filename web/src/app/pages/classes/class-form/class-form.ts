import { Component, EventEmitter, Input, OnChanges, OnInit, SimpleChanges, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BackendService } from '../../../util/backend.service';
import { SchoolClass, SchoolClassPayload } from '../class';
import { AcademicYear } from '../../academic-years/academic-year';

@Component({
  selector: 'app-class-form',
  standalone: false,
  templateUrl: './class-form.html',
  styleUrl: './class-form.scss'
})
export class ClassForm implements OnInit, OnChanges {

  @Input() visible = false;
  @Input() editingClass: SchoolClass | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() classCreated = new EventEmitter<SchoolClassPayload>();
  @Output() classUpdated = new EventEmitter<{ id: number; payload: SchoolClassPayload }>();

  classForm: FormGroup;
  academicYears: AcademicYear[] = [];
  teachers: {
    id: number;
    firstName: string;
    lastName: string;
  }[] = [];

  constructor(private fb: FormBuilder, private backend: BackendService) {
    this.classForm = this.fb.group({
      name: ['', Validators.required],
      gradeLevel: [null, Validators.required],
      academicYearId: [null, Validators.required],
      classTeacherId: [null]
    });
  }

  get isEditMode(): boolean {
    return this.editingClass !== null;
  }

  ngOnInit(): void {
    this.backend.get<AcademicYear[]>('/academic-years').subscribe({
      next: (years) => this.academicYears = years
    });

    this.backend.get<{
      id: number;
      firstName: string;
      lastName: string;
    }[]>('/users/teachers').subscribe({
      next: (teachers) => this.teachers = teachers
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['editingClass']) {
      if (this.editingClass) {
        this.classForm.patchValue({
          name: this.editingClass.name,
          gradeLevel: this.editingClass.gradeLevel,
          academicYearId: this.editingClass.academicYearId,
          classTeacherId: this.editingClass.classTeacherId ?? null
        });
      } else {
        this.classForm.reset({
          name: '',
          gradeLevel: null,
          academicYearId: null,
          classTeacherId: null
        });
      }
    }
  }

  onSubmit(): void {
    if (this.classForm.invalid) {
      this.classForm.markAllAsTouched();
      return;
    }

    const formValue = this.classForm.value;

    const payload: SchoolClassPayload = {
      name: formValue.name,
      gradeLevel: formValue.gradeLevel,
      academicYearId: formValue.academicYearId,
      classTeacherId: formValue.classTeacherId || null
    };

    if (this.isEditMode && this.editingClass) {
      this.classUpdated.emit({ id: this.editingClass.id, payload });
    } else {
      this.classCreated.emit(payload);
    }

    this.classForm.reset({
      name: '',
      gradeLevel: null,
      academicYearId: null,
      classTeacherId: null
    });
  }

  onClose(): void {
    this.classForm.reset({
      name: '',
      gradeLevel: null,
      academicYearId: null,
      classTeacherId: null
    });

    this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }
}
