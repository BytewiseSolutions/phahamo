import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Teacher } from '../teacher';
import { EmploymentType } from '../employment-type';
import { SubjectClassAssignment } from '../../subject-class-assignment/subject-class-assignment';
import { Period } from '../../timetable/period';
import { Timetable } from '../../timetable/timetable';
import { DayOfWeek } from '../../timetable/day-of-week';

@Component({
  selector: 'app-teacher-view',
  standalone: false,
  templateUrl: './teacher-view.html',
  styleUrl: './teacher-view.scss'
})
export class TeacherView implements OnChanges {

  @Input() teacher: Teacher | null = null;
  @Output() back = new EventEmitter<void>();

  activeTab: 'subjects' | 'timetable' | 'attendance' = 'subjects';

  assignments: SubjectClassAssignment[] = [];
  loadingAssignments = false;
  assignmentsError = '';

  periods: Period[] = [];
  timetableSlots: Timetable[] = [];
  loadingTimetable = false;
  timetableError = '';
  timetableLoaded = false;

  days = Object.values(DayOfWeek);

  constructor(
    private backend: BackendService
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['teacher'] && this.teacher) {
      this.loadAssignments();
      this.timetableLoaded = false;
    }
  }

  loadAssignments(): void {
    if (!this.teacher?.user?.id) {
      this.assignments = [];
      return;
    }

    this.loadingAssignments = true;
    this.assignmentsError = '';

    this.backend.get<SubjectClassAssignment[]>(
      `/subject-class-assignments/teacher/${this.teacher.user.id}`
    ).subscribe({
      next: (assignments) => {
        this.assignments = assignments;
        this.loadingAssignments = false;
      },
      error: (err) => {
        this.assignmentsError =
          err.error?.message || 'Failed to load teacher subjects.';
        this.loadingAssignments = false;
      }
    });
  }

  loadTimetable(): void {
    if (!this.teacher?.user?.id || this.timetableLoaded) {
      return;
    }

    this.loadingTimetable = true;
    this.timetableError = '';

    this.backend.get<Period[]>('/periods').subscribe({
      next: (periods) => {
        this.periods = periods;

        this.backend.get<Timetable[]>(
          `/timetable/teacher/${this.teacher!.user.id}`
        ).subscribe({
          next: (slots) => {
            this.timetableSlots = slots;
            this.loadingTimetable = false;
            this.timetableLoaded = true;
          },
          error: (err) => {
            this.timetableError =
              err.error?.message || 'Failed to load timetable.';
            this.loadingTimetable = false;
          }
        });
      },
      error: (err) => {
        this.timetableError =
          err.error?.message || 'Failed to load periods.';
        this.loadingTimetable = false;
      }
    });
  }

  slotFor(day: DayOfWeek, periodId: number): Timetable | null {
    return this.timetableSlots.find(slot =>
      slot.dayOfWeek === day && slot.periodId === periodId
    ) || null;
  }

  onTabChange(
    tab: 'subjects' | 'timetable' | 'attendance'
  ): void {
    this.activeTab = tab;

    if (tab === 'timetable') {
      this.loadTimetable();
    }
  }

  getDayLabel(day: DayOfWeek): string {
    return day.charAt(0) + day.slice(1).toLowerCase();
  }

  onBack(): void {
    this.back.emit();
  }

  getEmploymentTypeLabel(type: EmploymentType): string {
    switch (type) {
      case EmploymentType.FULL_TIME:
        return 'Full Time';

      case EmploymentType.PART_TIME:
        return 'Part Time';

      case EmploymentType.CONTRACT:
        return 'Contract';

      default:
        return type;
    }
  }
}
