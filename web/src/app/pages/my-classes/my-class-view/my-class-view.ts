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

  activeTab: 'timetable' | 'lessons' = 'timetable';

  periods: Period[] = [];
  timetableSlots: Timetable[] = [];
  loadingTimetable = false;

  days = Object.values(DayOfWeek);
  activeDay: DayOfWeek = DayOfWeek.MONDAY;

  lessons: Lesson[] = [];
  loadingLessons = false;

  lessonFilter: 'today' | 'week' | 'month' | 'quarter' | 'custom' = 'today';
  customStartDate: string = new Date().toISOString().substring(0, 10);
  customEndDate: string = new Date().toISOString().substring(0, 10);

  lessonStatuses = Object.values(LessonStatus);

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

  getDayLabel(day: DayOfWeek): string {
    return day.charAt(0) + day.slice(1).toLowerCase();
  }

  onDayChange(day: DayOfWeek): void {
    this.activeDay = day;
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

  onLessonStatusChange(lesson: Lesson, status: LessonStatus): void {
    this.errorMessage = '';

    this.backend.patch<Lesson>(`/lessons/${lesson.id}/status?status=${status}`, {}).subscribe({
      next: () => this.loadLessons(),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update lesson status.';
      }
    });
  }

  getLessonStatusLabel(status: LessonStatus): string {
    switch (status) {
      case LessonStatus.SCHEDULED: return 'Scheduled';
      case LessonStatus.CANCELLED: return 'Cancelled';
      case LessonStatus.SUBSTITUTED: return 'Substituted';
      case LessonStatus.MISSED: return 'Missed';
      default: return status;
    }
  }
}