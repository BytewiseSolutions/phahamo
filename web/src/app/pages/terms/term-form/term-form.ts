import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Term, TermPayload } from '../term';

@Component({
  selector: 'app-term-form',
  standalone: false,
  templateUrl: './term-form.html',
  styleUrl: './term-form.scss'
})
export class TermForm implements OnChanges {

  @Input() visible = false;
  @Input() academicYearId!: number;
  @Input() editingTerm: Term | null = null;

  @Output() closed = new EventEmitter<void>();

  @Output() termCreated = new EventEmitter<TermPayload>();

  @Output() termUpdated = new EventEmitter<{
    id: number;
    payload: TermPayload;
  }>();

  termForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.termForm = this.fb.group({
      name: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required]
    });
  }

  get isEditMode(): boolean {
    return this.editingTerm !== null;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['editingTerm']) {
      if (this.editingTerm) {
        this.termForm.patchValue({
          name: this.editingTerm.name,
          startDate: this.editingTerm.startDate?.substring(0, 10),
          endDate: this.editingTerm.endDate?.substring(0, 10)
        });
      } else {
        this.resetForm();
      }
    }
  }

  onSubmit(): void {
    if (this.termForm.invalid) {
      this.termForm.markAllAsTouched();
      return;
    }

    const formValue = this.termForm.value;

    const payload: TermPayload = {
      academicYearId: this.academicYearId,
      name: formValue.name,
      startDate: new Date(formValue.startDate).toISOString(),
      endDate: new Date(formValue.endDate).toISOString()
    };

    if (this.isEditMode && this.editingTerm) {
      this.termUpdated.emit({
        id: this.editingTerm.id,
        payload
      });
    } else {
      this.termCreated.emit(payload);
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
    this.termForm.reset({
      name: '',
      startDate: '',
      endDate: ''
    });
  }
}
