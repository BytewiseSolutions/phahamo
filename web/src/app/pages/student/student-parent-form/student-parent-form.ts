import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ParentPayload } from '../../parent/parent';

@Component({
  selector: 'app-student-parent-form',
  standalone: false,
  templateUrl: './student-parent-form.html',
  styleUrl: './student-parent-form.scss'
})
export class StudentParentForm implements OnChanges {

  @Input() visible = false;

  @Output() closed = new EventEmitter<void>();
  @Output() parentCreated = new EventEmitter<ParentPayload>();

  parentForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.parentForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.pattern(/^\S+\s+\S+.*$/)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      relationship: ['', Validators.required]
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible) {
      this.resetForm();
    }
  }

  onSubmit(): void {
    if (this.parentForm.invalid) {
      this.parentForm.markAllAsTouched();
      return;
    }

    const formValue = this.parentForm.getRawValue();
    const { firstName, lastName } = this.splitFullName(formValue.fullName);

    const payload: ParentPayload = {
      firstName,
      lastName,
      email: formValue.email,
      phone: formValue.phone,
      relationship: formValue.relationship
    };

    this.parentCreated.emit(payload);
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

  private splitFullName(fullName: string): { firstName: string; lastName: string } {
    const trimmed = fullName.trim();
    const firstSpaceIndex = trimmed.indexOf(' ');
    if (firstSpaceIndex === -1) return { firstName: trimmed, lastName: '' };
    return {
      firstName: trimmed.substring(0, firstSpaceIndex),
      lastName: trimmed.substring(firstSpaceIndex + 1).trim()
    };
  }

  private resetForm(): void {
    this.parentForm.reset({
      fullName: '',
      email: '',
      phone: '',
      relationship: ''
    });
  }
}
