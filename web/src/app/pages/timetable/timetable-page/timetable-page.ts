import { Component, OnInit } from '@angular/core';
import {DayOfWeek} from '../day-of-week';
import {Period, PeriodPayload} from '../period';
import {SchoolClass} from '../../class/class';
import {Timetable, TimetablePayload} from '../timetable';
import {BackendService} from '../../../util/backend.service';

@Component({
  selector: 'app-timetable-page',
  standalone: false,
  templateUrl: './timetable-page.html',
  styleUrl: './timetable-page.scss'
})
export class TimetablePage implements OnInit {

  activeTab: 'schedule' | 'periods' = 'schedule';

  activeDay: DayOfWeek = DayOfWeek.MONDAY;
  days = Object.values(DayOfWeek);

  periods: Period[] = [];
  schoolClasses: SchoolClass[] = [];
  timetableSlots: Timetable[] = [];

  loadingPeriods = false;
  loadingClasses = false;
  loadingTimetable = false;
  errorMessage = '';

  showPeriodForm = false;
  editingPeriod: Period | null = null;

  showTimetableForm = false;
  editingTimetable: Timetable | null = null;
  prefillDayOfWeek: DayOfWeek | null = null;
  prefillPeriodId: number | null = null;

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.loadPeriods();
    this.loadSchoolClasses();
    this.loadTimetable();
  }

  onTabChange(tab: 'schedule' | 'periods'): void {
    this.activeTab = tab;
  }

  onDayChange(day: DayOfWeek): void {
    this.activeDay = day;
  }

  getDayLabel(day: DayOfWeek): string {
    return day.charAt(0) + day.slice(1).toLowerCase();
  }

  loadPeriods(): void {
    this.loadingPeriods = true;

    this.backend.get<Period[]>('/periods').subscribe({
      next: (periods) => {
        this.periods = periods;
        this.loadingPeriods = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load periods.';
        this.loadingPeriods = false;
      }
    });
  }

  togglePeriodForm(): void {
    this.editingPeriod = null;
    this.showPeriodForm = !this.showPeriodForm;
    this.errorMessage = '';
  }

  onEditPeriod(period: Period): void {
    this.editingPeriod = period;
    this.showPeriodForm = true;
    this.errorMessage = '';
  }

  onPeriodFormClosed(): void {
    this.showPeriodForm = false;
    this.editingPeriod = null;
  }

  onPeriodCreated(payload: PeriodPayload): void {
    this.errorMessage = '';

    this.backend.postSilently<Period>('/periods', payload).subscribe({
      next: () => {
        this.showPeriodForm = false;
        this.loadPeriods();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to create period.';
      }
    });
  }

  onPeriodUpdated(event: { id: number; payload: PeriodPayload }): void {
    this.errorMessage = '';

    this.backend.putSilently<Period>(`/periods/${event.id}`, event.payload).subscribe({
      next: () => {
        this.showPeriodForm = false;
        this.editingPeriod = null;
        this.loadPeriods();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update period.';
      }
    });
  }

  loadSchoolClasses(): void {
    this.loadingClasses = true;

    this.backend.get<SchoolClass[]>('/classes').subscribe({
      next: (classes) => {
        this.schoolClasses = classes;
        this.loadingClasses = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load classes.';
        this.loadingClasses = false;
      }
    });
  }

  loadTimetable(): void {
    this.loadingTimetable = true;

    this.backend.get<Timetable[]>('/timetable').subscribe({
      next: (slots) => {
        this.timetableSlots = slots;
        this.loadingTimetable = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load timetable.';
        this.loadingTimetable = false;
      }
    });
  }

  slotFor(day: DayOfWeek, periodId: number, schoolClassName: string): Timetable | null {
    return this.timetableSlots.find(slot =>
      slot.dayOfWeek === day &&
      slot.periodId === periodId &&
      slot.schoolClassName === schoolClassName
    ) || null;
  }

  toggleTimetableForm(): void {
    this.editingTimetable = null;
    this.prefillDayOfWeek = null;
    this.prefillPeriodId = null;
    this.showTimetableForm = !this.showTimetableForm;
    this.errorMessage = '';
  }

  onAddSlotForCell(day: DayOfWeek, periodId: number): void {
    this.editingTimetable = null;
    this.prefillDayOfWeek = day;
    this.prefillPeriodId = periodId;
    this.showTimetableForm = true;
    this.errorMessage = '';
  }

  onEditTimetable(slot: Timetable): void {
    this.editingTimetable = slot;
    this.prefillDayOfWeek = null;
    this.prefillPeriodId = null;
    this.showTimetableForm = true;
    this.errorMessage = '';
  }

  onTimetableFormClosed(): void {
    this.showTimetableForm = false;
    this.editingTimetable = null;
    this.prefillDayOfWeek = null;
    this.prefillPeriodId = null;
  }

  onTimetableCreated(payload: TimetablePayload): void {
    this.errorMessage = '';

    this.backend.postSilently<Timetable>('/timetable', payload).subscribe({
      next: () => {
        this.showTimetableForm = false;
        this.prefillDayOfWeek = null;
        this.prefillPeriodId = null;
        this.loadTimetable();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to create timetable slot.';
      }
    });
  }

  onTimetableUpdated(event: { id: number; payload: TimetablePayload }): void {
    this.errorMessage = '';

    this.backend.putSilently<Timetable>(`/timetable/${event.id}`, event.payload).subscribe({
      next: () => {
        this.showTimetableForm = false;
        this.editingTimetable = null;
        this.loadTimetable();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update timetable slot.';
      }
    });
  }

  onDeleteTimetable(slot: Timetable): void {
    this.errorMessage = '';

    this.backend.delete<void>(`/timetable/${slot.id}`).subscribe({
      next: () => {
        this.loadTimetable();
      },
      error: (err: any) => {
        this.errorMessage = err.error?.message || 'Failed to delete timetable slot.';
      }
    });
  }
}
