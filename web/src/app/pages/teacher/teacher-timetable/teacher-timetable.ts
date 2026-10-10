import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Timetable } from '../../timetable/timetable';
import { Period } from '../../timetable/period';
import { DayOfWeek } from '../../timetable/day-of-week';

@Component({
  selector: 'app-teacher-timetable',
  standalone: false,
  templateUrl: './teacher-timetable.html',
  styleUrl: './teacher-timetable.scss'
})
export class TeacherTimetable implements OnChanges {

  @Input() teacherUserId!: number;

  periods: Period[] = [];
  slots: Timetable[] = [];
  loading = false;
  errorMessage = '';

  days = Object.values(DayOfWeek);

  private loaded = false;

  constructor(private backend: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['teacherUserId'] && this.teacherUserId && !this.loaded) {
      this.load();
    }
  }

  load(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<Period[]>('/periods').subscribe({
      next: (periods) => {
        this.periods = periods;

        this.backend.get<Timetable[]>(`/timetable/teacher/${this.teacherUserId}`).subscribe({
          next: (slots) => {
            this.slots = slots;
            this.loading = false;
            this.loaded = true;
          },
          error: (err) => {
            this.errorMessage = err.error?.message || 'Failed to load timetable.';
            this.loading = false;
          }
        });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load periods.';
        this.loading = false;
      }
    });
  }

  slotFor(day: DayOfWeek, periodId: number): Timetable | null {
    return this.slots.find(s => s.dayOfWeek === day && s.periodId === periodId) || null;
  }

  getDayLabel(day: DayOfWeek): string {
    return day.charAt(0) + day.slice(1).toLowerCase();
  }

  isBreak(period: Period): boolean {
    return period.name === 'Break' || period.name === 'Lunch';
  }
}
