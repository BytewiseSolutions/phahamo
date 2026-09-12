import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from '../subject';

@Component({
  selector: 'app-subject-form',
  standalone: false,
  templateUrl: './subject-form.html',
  styleUrl: './subject-form.scss'
})
export class SubjectForm implements OnChanges {

  @Input() visible = false;
  @Input() editingSubject: Subject | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() subjectCreated = new EventEmitter<any>();
  @Output() subjectUpdated = new EventEmitter<{
    id: number;
    payload: any;
  }>();

  subjectForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.subjectForm = this.fb.group({
      name: ['', Validators.required],
      code: ['', Validators.required]
    });
  }

  get isEditMode(): boolean {
    return this.editingSubject !== null;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['editingSubject']) {

      if (this.editingSubject) {

        this.subjectForm.patchValue({
          name: this.editingSubject.name,
          code: this.editingSubject.code
        });

      } else {

        this.subjectForm.reset();

      }
    }
  }

  onSubmit(): void {
    if (this.subjectForm.invalid) {
      this.subjectForm.markAllAsTouched();
      return;
    }

    const formValue = this.subjectForm.value;

    if (this.isEditMode && this.editingSubject) {

      this.subjectUpdated.emit({
        id: this.editingSubject.id,
        payload: {
          name: formValue.name,
          code: formValue.code
        }
      });

    } else {

      this.subjectCreated.emit({
        name: formValue.name,
        code: formValue.code
      });

    }

    this.subjectForm.reset();
  }

  onClose(): void {
    this.subjectForm.reset();
    this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }
}
