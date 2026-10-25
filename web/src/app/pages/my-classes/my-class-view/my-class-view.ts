import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { AuthService } from '../../../util/auth.service';
import { SubjectClassAssignment } from '../../subject-class-assignment/subject-class-assignment';
import { Period } from '../../timetable/period';
import { Timetable } from '../../timetable/timetable';
import { DayOfWeek } from '../../timetable/day-of-week';
import { Lesson } from '../../timetable/lesson';
import { LessonStatus } from '../../timetable/lesson-status';
import { Teacher } from '../../teacher/teacher';
import { Holiday } from '../../holidays/holiday';

@Component({
  selector: 'app-my-class-view',
  standalone: false,
  templateUrl: './my-class-view.html',
  styleUrl: './my-class-view.scss'
})
export class MyClassView implements OnInit {

  assignment: SubjectClassAssignment | null = null;
  loadingAssignment = true;
  notFound = false;
  errorMessage = '';
  holidays: Holiday[] = [];
  holidayName: string | null = null;

  activeTab: 'timetable' | 'lessons' = 'timetable';

  periods: Period[] = [];
  timetableSlots: Timetable[] = [];
  loadingTimetable = false;

  days = Object.values(DayOfWeek);
  activeDay: DayOfWeek = this.getTodayDayOfWeek();

  lessons: Lesson[] = [];
  loadingLessons = false;

  lessonFilter: 'today' | 'week' | 'month' | 'quarter' | 'custom' = 'today';
  customStartDate: string = new Date().toISOString().substring(0, 10);
  customEndDate: string = new Date().toISOString().substring(0, 10);

  lessonStatuses = Object.values(LessonStatus);

  substitutionLesson: Lesson | null = null;
  availableSubstituteTeachers: Teacher[] = [];
  loadingSubstituteTeachers = false;
  substitutionError = '';

  private userId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private backend: BackendService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.userId = this.authService.getUserId();

    const assignmentId = Number(this.route.snapshot.paramMap.get('assignmentId'));
    this.loadAssignment(assignmentId);
  }

  loadAssignment(assignmentId: number): void {
    this.loadingAssignment = true;

    this.backend.get<SubjectClassAssignment[]>('/subject-class-assignments/my-assignments').subscribe({
      next: (assignments) => {
        this.assignment = assignments.find(a => a.id === assignmentId) || null;
        this.loadingAssignment = false;

        if (!this.assignment) {
          this.notFound = true;
        } else {
          this.loadTimetable();
        }
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load class.';
        this.loadingAssignment = false;
      }
    });
  }

  onBack(): void {
    this.router.navigate(['/my-classes']);
  }

  onTabChange(tab: 'timetable' | 'lessons'): void {
    this.activeTab = tab;

    if (tab === 'lessons' && this.lessons.length === 0) {
      this.loadLessons();
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

  getDayLabel(day: DayOfWeek): string {
    return day.charAt(0) + day.slice(1).toLowerCase();
  }

  onDayChange(day: DayOfWeek): void {
    this.activeDay = day;
    this.loadHolidayForActiveDay();
  }

  // ----- Timetable -----

  loadTimetable(): void {
    if (!this.userId) {
      return;
    }

    this.loadingTimetable = true;

    this.backend.get<Period[]>('/periods').subscribe({
      next: (periods) => {
        this.periods = periods;

        this.backend.get<Timetable[]>(`/timetable/teacher/${this.userId}`).subscribe({
          next: (slots) => {
            this.timetableSlots = slots.filter(slot =>
              slot.schoolClassName === this.assignment?.schoolClassName &&
              slot.subjectName === this.assignment?.subjectName
            );
            this.loadingTimetable = false;
            this.loadHolidayForActiveDay();
          },
          error: (err) => {
            this.errorMessage = err.error?.message || 'Failed to load timetable.';
            this.loadingTimetable = false;
          }
        });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load periods.';
        this.loadingTimetable = false;
      }
    });
  }

  slotFor(day: DayOfWeek, periodId: number): Timetable | null {
    return this.timetableSlots.find(slot =>
      slot.dayOfWeek === day && slot.periodId === periodId
    ) || null;
  }

  // ----- Lessons -----

  onLessonFilterChange(filter: 'today' | 'week' | 'month' | 'quarter' | 'custom'): void {
    this.lessonFilter = filter;

    if (filter !== 'custom') {
      this.loadLessons();
    }
  }

  onCustomDateChange(): void {
    if (this.lessonFilter === 'custom') {
      this.loadLessons();
    }
  }

  private getDateRange(): { start: string; end: string } {
    const today = new Date();
    const start = new Date(today);
    const end = new Date(today);

    switch (this.lessonFilter) {
      case 'week':
        start.setDate(today.getDate() - today.getDay() + 1);
        end.setDate(start.getDate() + 4);
        break;
      case 'month':
        start.setDate(1);
        end.setMonth(today.getMonth() + 1, 0);
        break;
      case 'quarter': {
        const quarterStartMonth = Math.floor(today.getMonth() / 3) * 3;
        start.setMonth(quarterStartMonth, 1);
        end.setMonth(quarterStartMonth + 3, 0);
        break;
      }
      case 'custom':
        return { start: this.customStartDate, end: this.customEndDate };
      case 'today':
      default:
        break;
    }

    return {
      start: start.toISOString().substring(0, 10),
      end: end.toISOString().substring(0, 10)
    };
  }

  loadLessons(): void {
    if (!this.userId) {
      return;
    }

    this.loadingLessons = true;
    this.errorMessage = '';

    const applyFilter = (lessons: Lesson[]) => {
      this.lessons = lessons.filter(l =>
        l.schoolClassName === this.assignment?.schoolClassName &&
        l.subjectName === this.assignment?.subjectName
      );
      this.loadingLessons = false;
    };

    if (this.lessonFilter === 'today') {
      const today = new Date().toISOString().substring(0, 10);
      this.loadHolidays(today, today);

      this.backend.get<Lesson[]>(`/lessons/teacher/${this.userId}?date=${today}`).subscribe({
        next: applyFilter,
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to load lessons.';
          this.loadingLessons = false;
        }
      });

      return;
    }

    const { start, end } = this.getDateRange();
    this.loadHolidays(start, end);

    this.backend.get<Lesson[]>(
      `/lessons/teacher/${this.userId}/range?startDate=${start}&endDate=${end}`
    ).subscribe({
      next: applyFilter,
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load lessons.';
        this.loadingLessons = false;
      }
    });
  }

  private loadHolidays(from: string, to: string): void {
    this.backend.get<Holiday[]>(`/holidays?from=${from}&to=${to}`).subscribe({
      next: (holidays) => this.holidays = holidays,
      error: () => this.holidays = []
    });
  }

  private loadHolidayForActiveDay(): void {
    const date = this.dateForDay(this.activeDay);
    this.backend.get<Holiday[]>(`/holidays?from=${date}&to=${date}`).subscribe({
      next: (holidays) => this.holidayName = holidays[0]?.name ?? null,
      error: () => this.holidayName = null
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

  onAttendance(lesson: Lesson): void {
    this.router.navigate(['/my-classes', this.route.snapshot.paramMap.get('assignmentId'), 'attendance', lesson.id]);
  }

  onLessonStatusChange(lesson: Lesson, status: LessonStatus): void {
    this.errorMessage = '';

    if (status === LessonStatus.SUBSTITUTED) {
      this.openSubstitutionDialog(lesson);
      return;
    }

    this.backend.patch<Lesson>(`/lessons/${lesson.id}/status?status=${status}`, {}).subscribe({
      next: () => this.loadLessons(),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update lesson status.';
      }
    });
  }

  openSubstitutionDialog(lesson: Lesson): void {
    this.substitutionLesson = lesson;
    this.availableSubstituteTeachers = [];
    this.substitutionError = '';
    this.loadingSubstituteTeachers = true;

    this.backend.get<Teacher[]>(
      `/lessons/${lesson.id}/available-substitute-teachers`
    ).subscribe({
      next: (teachers) => {
        this.availableSubstituteTeachers = teachers;
        this.loadingSubstituteTeachers = false;
      },
      error: (err) => {
        this.substitutionError = err.error?.message || 'Failed to find available teachers.';
        this.loadingSubstituteTeachers = false;
      }
    });
  }

  closeSubstitutionDialog(): void {
    this.substitutionLesson = null;
    this.availableSubstituteTeachers = [];
    this.substitutionError = '';
  }

  confirmSubstitution(substituteTeacherUserId: number): void {
    if (!this.substitutionLesson) return;

    const lessonId = this.substitutionLesson.id;
    this.backend.patch<Lesson>(
      `/lessons/${lessonId}/status?status=${LessonStatus.SUBSTITUTED}&substituteTeacherUserId=${substituteTeacherUserId}`,
      {}
    ).subscribe({
      next: () => {
        this.closeSubstitutionDialog();
        this.loadLessons();
      },
      error: (err) => {
        this.substitutionError = err.error?.message || 'Failed to substitute lesson.';
      }
    });
  }

  getLessonStatusLabel(status: LessonStatus): string {
    switch (status) {
      case LessonStatus.SCHEDULED: return 'Scheduled';
      case LessonStatus.COMPLETED: return 'Completed';
      case LessonStatus.CANCELLED: return 'Cancelled';
      case LessonStatus.SUBSTITUTED: return 'Substituted';
      case LessonStatus.MISSED: return 'Missed';
      default: return status;
    }
  }
}
