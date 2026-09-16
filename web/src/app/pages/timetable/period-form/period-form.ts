import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Period, PeriodPayload } from '../period';

@Component({
  selector: 'app-period-form',
  standalone: false,
  templateUrl: './period-form.html',
  styleUrl: './period-form.scss'
})
export class PeriodForm implements OnChanges {

  @Input() visible = false;
  @Input() editingPeriod: Period | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() periodCreated = new EventEmitter<PeriodPayload>();
  @Output() periodUpdated = new EventEmitter<{ id: number; payload: PeriodPayload }>();

  periodForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.periodForm = this.fb.group({
      name: ['', Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required]
    });
  }

  get isEditMode(): boolean {
    return this.editingPeriod !== null;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['editingPeriod']) {
      if (this.editingPeriod) {
        this.periodForm.patchValue({
          name: this.editingPeriod.name,
          startTime: this.editingPeriod.startTime?.substring(0, 5) || '',
          endTime: this.editingPeriod.endTime?.substring(0, 5) || ''
        });
      } else {
        this.resetForm();
      }
    }
  }

  onSubmit(): void {
    if (this.periodForm.invalid) {
      this.periodForm.markAllAsTouched();
      return;
    }

    const formValue = this.periodForm.getRawValue();

    const payload: PeriodPayload = {
      name: formValue.name.trim(),
      startTime: formValue.startTime,
      endTime: formValue.endTime
    };

    if (this.isEditMode && this.editingPeriod) {
      this.periodUpdated.emit({ id: this.editingPeriod.id, payload });
    } else {
      this.periodCreated.emit(payload);
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
    this.periodForm.reset({
      name: '',
      startTime: '',
      endTime: '',
      orderIndex: ''
    });
  }
}
