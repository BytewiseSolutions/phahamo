import { Component, OnInit } from '@angular/core';
import {DayOfWeek} from '../day-of-week';
import {Period, PeriodPayload} from '../period';
import {SchoolClass} from '../../class/class';
import {Teacher} from '../../teacher/teacher';
import {Timetable, TimetablePayload} from '../timetable';
import {BackendService} from '../../../util/backend.service';
import {AuthService} from '../../../util/auth.service';

@Component({
  selector: 'app-timetable-page',
  standalone: false,
  templateUrl: './timetable-page.html',
  styleUrl: './timetable-page.scss'
})
export class TimetablePage implements OnInit {

  isTeacher = false;
  activeTab: 'schedule' | 'periods' = 'schedule';
  isStudent = false;

  activeDay: DayOfWeek = this.getTodayDayOfWeek();
  days = Object.values(DayOfWeek);

  viewMode: 'class' | 'teacher' = 'class';

  periods: Period[] = [];
  schoolClasses: SchoolClass[] = [];
  teachers: Teacher[] = [];
  timetableSlots: Timetable[] = [];

  loadingPeriods = false;
  loadingClasses = false;
  loadingTeachers = false;
  loadingTimetable = false;
  errorMessage = '';

  showPeriodForm = false;
  editingPeriod: Period | null = null;

  showTimetableForm = false;
  editingTimetable: Timetable | null = null;
  prefillDayOfWeek: DayOfWeek | null = null;
  prefillPeriodId: number | null = null;

  private userId: number | null = null;

  constructor(private backend: BackendService, private authService: AuthService) {}

  ngOnInit(): void {
    this.isTeacher = this.authService.hasRole('TEACHER');
    this.isStudent = this.authService.hasRole('STUDENT');
    this.userId = this.authService.getUserId();

    this.loadPeriods();
    this.loadTimetable();

    if (!this.isTeacher && !this.isStudent) {
      this.loadSchoolClasses();
      this.loadTeachers();
    }
  }

  private getTodayDayOfWeek(): DayOfWeek {
    const map: Record<number, DayOfWeek> = {
      0: DayOfWeek.SUNDAY,
      1: DayOfWeek.MONDAY,
      2: DayOfWeek.TUESDAY,
      3: DayOfWeek.WEDNESDAY,
      4: DayOfWeek.THURSDAY,
      5: DayOfWeek.FRIDAY,
      6: DayOfWeek.SATURDAY
    };
    return map[new Date().getDay()];
  }

  onTabChange(tab: 'schedule' | 'periods'): void {
    this.activeTab = tab;
  }

  onDayChange(day: DayOfWeek): void {
    this.activeDay = day;
    if (this.isTeacher) {
      this.loadTimetable();
    }
  }

  onViewModeChange(mode: 'class' | 'teacher'): void {
    this.viewMode = mode;
  }

  getDayLabel(day: DayOfWeek): string {
    return day.charAt(0) + day.slice(1).toLowerCase();
  }

  getTeacherFullName(teacher: Teacher): string {
    return `${teacher.firstName} ${teacher.lastName}`;
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

  loadTeachers(): void {
    this.loadingTeachers = true;

    this.backend.get<Teacher[]>('/teachers').subscribe({
      next: (teachers) => {
        this.teachers = teachers;
        this.loadingTeachers = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load teachers.';
        this.loadingTeachers = false;
      }
    });
  }

  loadTimetable(): void {
    this.loadingTimetable = true;

    let url: string;
    if (this.isTeacher) {
      url = `/timetable/teacher/${this.userId}?date=${this.dateForDay(this.activeDay)}`;
    } else if (this.isStudent) {
      url = '/timetable/my-class';
    } else {
      url = '/timetable';
    }

    this.backend.get<Timetable[]>(url).subscribe({
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

  private dateForDay(day: DayOfWeek): string {
    const today = new Date();
    const currentDay = today.getDay() === 0 ? 7 : today.getDay();
    const targetDay = Object.values(DayOfWeek).indexOf(day) + 1;
    const date = new Date(today);
    date.setDate(today.getDate() + targetDay - currentDay);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const dayOfMonth = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${dayOfMonth}`;
  }

  slotForTeacherView(day: DayOfWeek, periodId: number): Timetable | null {
    return this.timetableSlots.find(slot =>
      slot.dayOfWeek === day && slot.periodId === periodId
    ) || null;
  }

  slotForClass(day: DayOfWeek, periodId: number, schoolClassName: string): Timetable | null {
    return this.timetableSlots.find(slot =>
      slot.dayOfWeek === day &&
      slot.periodId === periodId &&
      slot.schoolClassName === schoolClassName
    ) || null;
  }

  slotForTeacher(day: DayOfWeek, periodId: number, teacherFullName: string): Timetable | null {
    return this.timetableSlots.find(slot =>
      slot.dayOfWeek === day &&
      slot.periodId === periodId &&
      slot.teacherName === teacherFullName
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
