import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Teacher } from '../teacher';
import { EmploymentType } from '../employment-type';
import { SubjectClassAssignment } from '../../subject-class-assignment/subject-class-assignment';
import { Period } from '../../timetable/period';
import { Timetable } from '../../timetable/timetable';
import { DayOfWeek } from '../../timetable/day-of-week';
import { Lesson } from '../../timetable/lesson';
import { LessonStatus } from '../../timetable/lesson-status';

@Component({
  selector: 'app-teacher-view',
  standalone: false,
  templateUrl: './teacher-view.html',
  styleUrl: './teacher-view.scss'
})
export class TeacherView implements OnChanges {

  @Input() teacher: Teacher | null = null;
  @Output() back = new EventEmitter<void>();

  activeTab: 'subjects' | 'timetable' | 'lessons' | 'attendance' = 'subjects';

  assignments: SubjectClassAssignment[] = [];
  loadingAssignments = false;
  assignmentsError = '';

  periods: Period[] = [];
  timetableSlots: Timetable[] = [];
  loadingTimetable = false;
  timetableError = '';
  timetableLoaded = false;

  days = Object.values(DayOfWeek);

  lessons: Lesson[] = [];
  loadingLessons = false;
  lessonsError = '';

  lessonFilter: 'today' | 'week' | 'month' | 'quarter' | 'custom' = 'today';
  customStartDate: string = new Date().toISOString().substring(0, 10);
  customEndDate: string = new Date().toISOString().substring(0, 10);

  lessonStatuses = Object.values(LessonStatus);

  constructor(
    private backend: BackendService
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['teacher'] && this.teacher) {
      this.loadAssignments();
      this.timetableLoaded = false;
      this.lessons = [];
    }
  }

  // ----- Subjects -----

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

  // ----- Timetable -----

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
        start.setDate(today.getDate() - today.getDay() + 1); // Monday
        end.setDate(start.getDate() + 4); // Friday
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
    if (!this.teacher?.user?.id) {
      return;
    }

    this.loadingLessons = true;
    this.lessonsError = '';

    if (this.lessonFilter === 'today') {
      const today = new Date().toISOString().substring(0, 10);

      this.backend.get<Lesson[]>(
        `/lessons/teacher/${this.teacher.user.id}?date=${today}`
      ).subscribe({
        next: (lessons) => {
          this.lessons = lessons;
          this.loadingLessons = false;
        },
        error: (err) => {
          this.lessonsError = err.error?.message || 'Failed to load lessons.';
          this.loadingLessons = false;
        }
      });

      return;
    }

    const { start, end } = this.getDateRange();

    this.backend.get<Lesson[]>(
      `/lessons/teacher/${this.teacher.user.id}/range?startDate=${start}&endDate=${end}`
    ).subscribe({
      next: (lessons) => {
        this.lessons = lessons;
        this.loadingLessons = false;
      },
      error: (err) => {
        this.lessonsError = err.error?.message || 'Failed to load lessons.';
        this.loadingLessons = false;
      }
    });
  }

  onLessonStatusChange(lesson: Lesson, status: LessonStatus): void {
    this.lessonsError = '';

    this.backend.patch<Lesson>(
      `/lessons/${lesson.id}/status?status=${status}`,
      {}
    ).subscribe({
      next: () => {
        this.loadLessons();
      },
      error: (err) => {
        this.lessonsError = err.error?.message || 'Failed to update lesson status.';
      }
    });
  }

  getLessonStatusLabel(status: LessonStatus): string {
    switch (status) {
      case LessonStatus.SCHEDULED:
        return 'Scheduled';
      case LessonStatus.CANCELLED:
        return 'Cancelled';
      case LessonStatus.SUBSTITUTED:
        return 'Substituted';
      case LessonStatus.MISSED:
        return 'Missed';
      default:
        return status;
    }
  }

  onTabChange(
    tab: 'subjects' | 'timetable' | 'lessons' | 'attendance'
  ): void {
    this.activeTab = tab;

    if (tab === 'timetable') {
      this.loadTimetable();
    }

    if (tab === 'lessons') {
      this.loadLessons();
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
