import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Student, StudentPayload } from '../student';

@Component({
  selector: 'app-student-form',
  standalone: false,
  templateUrl: './student-form.html',
  styleUrl: './student-form.scss'
})
export class StudentForm implements OnChanges {

  @Input() visible = false;
  @Input() editingStudent: Student | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() studentCreated = new EventEmitter<StudentPayload>();
  @Output() studentUpdated = new EventEmitter<{ id: number; payload: StudentPayload }>();

  studentForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.studentForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      dateOfBirth: ['', Validators.required],
      gender: ['', Validators.required]
    });
  }

  get isEditMode(): boolean {
    return this.editingStudent !== null;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['editingStudent']) {
      if (this.editingStudent) {
        this.studentForm.patchValue({
          firstName: this.editingStudent.firstName,
          lastName: this.editingStudent.lastName,
          dateOfBirth: this.editingStudent.dateOfBirth?.substring(0, 10),
          gender: this.editingStudent.gender
        });
      } else {
        this.resetForm();
      }
    }
  }

  onSubmit(): void {
    if (this.studentForm.invalid) {
      this.studentForm.markAllAsTouched();
      return;
    }

    const formValue = this.studentForm.value;

    const payload: StudentPayload = {
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      dateOfBirth: new Date(formValue.dateOfBirth).toISOString(),
      gender: formValue.gender
    };

    if (this.isEditMode && this.editingStudent) {
      this.studentUpdated.emit({ id: this.editingStudent.id, payload });
    } else {
      this.studentCreated.emit(payload);
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
    this.studentForm.reset({
      firstName: '',
      lastName: '',
      dateOfBirth: '',
      gender: ''
    });
  }
}
