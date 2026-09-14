import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Parent, ParentPayload } from '../parent';

@Component({
  selector: 'app-parent-form',
  standalone: false,
  templateUrl: './parent-form.html',
  styleUrl: './parent-form.scss'
})
export class ParentForm implements OnChanges {

  @Input() visible = false;
  @Input() editingParent: Parent | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() parentCreated = new EventEmitter<ParentPayload>();
  @Output() parentUpdated = new EventEmitter<{ id: number; payload: ParentPayload }>();

  parentForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.parentForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required]
    });
  }

  get isEditMode(): boolean {
    return this.editingParent !== null;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['editingParent']) {
      if (this.editingParent) {
        this.parentForm.patchValue({
          firstName: this.editingParent.firstName,
          lastName: this.editingParent.lastName,
          email: this.editingParent.email,
          phone: this.editingParent.phone
        });
      } else {
        this.resetForm();
      }
    }
  }

  onSubmit(): void {
    if (this.parentForm.invalid) {
      this.parentForm.markAllAsTouched();
      return;
    }

    const formValue = this.parentForm.value;

    const payload: ParentPayload = {
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      email: formValue.email,
      phone: formValue.phone
    };

    if (this.isEditMode && this.editingParent) {
      this.parentUpdated.emit({ id: this.editingParent.id, payload });
    } else {
      this.parentCreated.emit(payload);
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
    this.parentForm.reset({
      firstName: '',
      lastName: '',
      email: '',
      phone: ''
    });
  }
}
