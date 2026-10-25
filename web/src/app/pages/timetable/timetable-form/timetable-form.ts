import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  SimpleChanges,
  Output
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Timetable, TimetablePayload } from '../timetable';
import { DayOfWeek } from '../day-of-week';
import { Period } from '../period';
import { SubjectClassAssignment } from '../../subject-class-assignment/subject-class-assignment';
import { BackendService } from '../../../util/backend.service';

@Component({
  selector: 'app-timetable-form',
  standalone: false,
  templateUrl: './timetable-form.html',
  styleUrl: './timetable-form.scss'
})
export class TimetableForm implements OnChanges {

  @Input() visible = false;
  @Input() editingTimetable: Timetable | null = null;
  @Input() periods: Period[] = [];

  @Input() prefillDayOfWeek: DayOfWeek | null = null;
  @Input() prefillPeriodId: number | null = null;
  @Input() prefillSchoolClassId: number | null = null;
  @Input() timetableSlots: Timetable[] = [];

  @Output() closed = new EventEmitter<void>();
  @Output() timetableCreated = new EventEmitter<TimetablePayload>();
  @Output() timetableUpdated = new EventEmitter<{ id: number; payload: TimetablePayload }>();

  timetableForm: FormGroup;

  daysOfWeek = Object.values(DayOfWeek);

  assignments: SubjectClassAssignment[] = [];
  loadingAssignments = false;
  assignmentLoadError = '';

  constructor(
    private fb: FormBuilder,
    private backend: BackendService
  ) {
    this.timetableForm = this.fb.group({
      dayOfWeek: ['', Validators.required],
      periodId: ['', Validators.required],
      assignmentId: ['', Validators.required]
    });
  }

  get isEditMode(): boolean {
    return this.editingTimetable !== null;
  }

  get availableAssignments(): SubjectClassAssignment[] {
    const selectedDay = this.timetableForm.get('dayOfWeek')?.value as DayOfWeek | null;
    const selectedPeriodId = Number(this.timetableForm.get('periodId')?.value);
    const currentAssignmentId = this.editingTimetable?.assignmentId;

    return this.assignments.filter(assignment => {
      const isCurrentAssignment = assignment.id === currentAssignmentId;

      if (this.prefillSchoolClassId !== null &&
          assignment.schoolClassId !== this.prefillSchoolClassId &&
          !isCurrentAssignment) {
        return false;
      }

      if (!selectedDay || !selectedPeriodId || isCurrentAssignment) {
        return true;
      }

      const assignmentSlotsToday = this.timetableSlots.filter(slot =>
        slot.id !== this.editingTimetable?.id &&
        slot.dayOfWeek === selectedDay &&
        slot.assignmentId === assignment.id
      );

      if (assignmentSlotsToday.length >= 2) {
        return false;
      }

      return !this.timetableSlots.some(slot => {
        if (slot.id === this.editingTimetable?.id ||
            slot.dayOfWeek !== selectedDay ||
            slot.periodId !== selectedPeriodId) {
          return false;
        }

        const sameClass = slot.schoolClassId === assignment.schoolClassId;
        const sameTeacher = slot.teacherId != null &&
          assignment.teacherId != null &&
          slot.teacherId === assignment.teacherId;

        return sameClass || sameTeacher;
      });
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible) {
      this.loadAssignments();
    }

    if (
      this.visible &&
      (changes['visible'] || changes['editingTimetable'] ||
        changes['prefillDayOfWeek'] || changes['prefillPeriodId'] ||
        changes['prefillSchoolClassId'] || changes['timetableSlots'])
    ) {
      this.applyFormState();
    }
  }

  private applyFormState(): void {
    if (this.editingTimetable) {
      this.timetableForm.patchValue({
        dayOfWeek: this.editingTimetable.dayOfWeek,
        periodId: this.editingTimetable.periodId,
        assignmentId: this.editingTimetable.assignmentId
      });
      return;
    }

    this.resetForm();
    this.timetableForm.patchValue({
      dayOfWeek: this.prefillDayOfWeek || '',
      periodId: this.prefillPeriodId || ''
    });
  }

  private loadAssignments(): void {
    this.loadingAssignments = true;
    this.assignmentLoadError = '';

    this.backend.get<SubjectClassAssignment[]>('/subject-class-assignments').subscribe({
      next: (assignments) => {
        this.assignments = assignments;
        this.loadingAssignments = false;
      },
      error: (err) => {
        this.assignments = [];
        this.assignmentLoadError =
          err.error?.message || 'Failed to load subject-class assignments.';
        this.loadingAssignments = false;
      }
    });
  }

  onSubmit(): void {
    if (this.timetableForm.invalid) {
      this.timetableForm.markAllAsTouched();
      return;
    }

    const formValue = this.timetableForm.getRawValue();

    const payload: TimetablePayload = {
      dayOfWeek: formValue.dayOfWeek,
      periodId: Number(formValue.periodId),
      assignmentId: Number(formValue.assignmentId)
    };

    if (this.isEditMode && this.editingTimetable) {
      this.timetableUpdated.emit({ id: this.editingTimetable.id, payload });
    } else {
      this.timetableCreated.emit(payload);
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

  getDayLabel(day: DayOfWeek): string {
    return day.charAt(0) + day.slice(1).toLowerCase();
  }

  private resetForm(): void {
    this.timetableForm.reset({
      dayOfWeek: '',
      periodId: '',
      assignmentId: ''
    });
  }
}
