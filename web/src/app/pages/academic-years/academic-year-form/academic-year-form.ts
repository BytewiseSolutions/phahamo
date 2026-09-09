import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {AcademicYear, AcademicYearPayload} from '../academic-year';

@Component({
  selector: 'app-academic-year-form',
  standalone: false,
  templateUrl: './academic-year-form.html',
  styleUrl: './academic-year-form.scss'
})
export class AcademicYearForm implements OnChanges {

  @Input() visible = false;
  @Input() editingAcademicYear: AcademicYear | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() academicYearCreated = new EventEmitter<AcademicYearPayload>();
  @Output() academicYearUpdated = new EventEmitter<{
    id: number;
    payload: AcademicYearPayload;
  }>();

  academicYearForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.academicYearForm = this.fb.group({
      name: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      active: [false]
    });
  }

  get isEditMode(): boolean {
    return this.editingAcademicYear !== null;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['editingAcademicYear']) {
      if (this.editingAcademicYear) {
        this.academicYearForm.patchValue({
          name: this.editingAcademicYear.name,
          startDate: this.editingAcademicYear.startDate?.substring(0, 10),
          endDate: this.editingAcademicYear.endDate?.substring(0, 10),
          active: this.editingAcademicYear.active
        });

        if (this.editingAcademicYear.active) {
          this.academicYearForm.get('active')?.disable();
        } else {
          this.academicYearForm.get('active')?.enable();
        }
      } else {
        this.academicYearForm.get('active')?.enable();
        this.academicYearForm.reset({
          name: '',
          startDate: '',
          endDate: '',
          active: false
        });
      }
    }
  }

  onSubmit(): void {
    if (this.academicYearForm.invalid) {
      this.academicYearForm.markAllAsTouched();
      return;
    }

    const formValue = this.academicYearForm.value;

    const payload: AcademicYearPayload = {
      name: formValue.name,
      startDate: new Date(formValue.startDate).toISOString(),
      endDate: new Date(formValue.endDate).toISOString(),
      active: formValue.active
    };

    if (this.isEditMode && this.editingAcademicYear) {
      this.academicYearUpdated.emit({
        id: this.editingAcademicYear.id,
        payload
      });
    } else {
      this.academicYearCreated.emit(payload);
    }

    this.academicYearForm.reset({
      name: '',
      startDate: '',
      endDate: '',
      active: false
    });
  }

  onClose(): void {
    this.academicYearForm.reset({
      name: '',
      startDate: '',
      endDate: '',
      active: false
    });

    this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }
}
