import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Lesson } from '../../timetable/lesson';
import { LessonStatus } from '../../timetable/lesson-status';
import { Attendance } from '../../attendance/attendance';
import { Teacher } from '../teacher';

type LessonFilter = 'today' | 'week' | 'month' | 'quarter' | 'custom';

interface AttendanceSummary {
  present: number;
  total: number;
  unmarked: number;
}

@Component({
  selector: 'app-teacher-lessons',
  standalone: false,
  templateUrl: './teacher-lessons.html',
  styleUrl: './teacher-lessons.scss'
})
export class TeacherLessons implements OnChanges {

  @Input() teacherUserId!: number;

  lessons: Lesson[] = [];
  attendanceByLesson: Record<number, AttendanceSummary> = {};
  loading = false;
  errorMessage = '';

  lessonFilter: LessonFilter = 'today';
  customStartDate: string = new Date().toISOString().substring(0, 10);
  customEndDate: string = new Date().toISOString().substring(0, 10);

  lessonStatuses = Object.values(LessonStatus);

  substitutionLesson: Lesson | null = null;
  availableSubstituteTeachers: Teacher[] = [];
  loadingSubstituteTeachers = false;
  substitutionError = '';

  private loaded = false;

  constructor(private backend: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['teacherUserId'] && this.teacherUserId && !this.loaded) {
      this.loadLessons();
    }
  }

  onFilterChange(filter: LessonFilter): void {
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

  loadLessons(): void {
    this.loading = true;
    this.errorMessage = '';
    this.attendanceByLesson = {};

    if (this.lessonFilter === 'today') {
      const today = new Date().toISOString().substring(0, 10);

      this.backend.get<Lesson[]>(
        `/lessons/teacher/${this.teacherUserId}?date=${today}`
      ).subscribe({
        next: (lessons) => {
          this.lessons = lessons;
          this.loading = false;
          this.loaded = true;
          this.loadAttendanceForAll();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to load lessons.';
          this.loading = false;
        }
      });

      return;
    }

    const { start, end } = this.getDateRange();

    this.backend.get<Lesson[]>(
      `/lessons/teacher/${this.teacherUserId}/range?startDate=${start}&endDate=${end}`
    ).subscribe({
      next: (lessons) => {
        this.lessons = lessons;
        this.loading = false;
        this.loaded = true;
        this.loadAttendanceForAll();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load lessons.';
        this.loading = false;
      }
    });
  }

  private loadAttendanceForAll(): void {
    for (const lesson of this.lessons) {
      this.loadAttendanceFor(lesson.id);
    }
  }

  private loadAttendanceFor(lessonId: number): void {
    this.backend.get<Attendance[]>(`/attendance/lesson/${lessonId}`).subscribe({
      next: (records) => {
        this.attendanceByLesson[lessonId] = this.summarize(records);
      },
      error: () => {
        this.attendanceByLesson[lessonId] = { present: 0, total: 0, unmarked: 0 };
      }
    });
  }

  private summarize(records: Attendance[]): AttendanceSummary {
    const total = records.length;
    const present = records.filter(r =>
      r.status === 'PRESENT' || r.status === 'LATE'
    ).length;
    const unmarked = records.filter(r => r.status === null).length;

    return { present, total, unmarked };
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

  onStatusChange(lesson: Lesson, status: LessonStatus): void {
    this.errorMessage = '';

    if (status === LessonStatus.SUBSTITUTED) {
      this.openSubstitutionDialog(lesson);
      return;
    }

    this.backend.patch<Lesson>(
      `/lessons/${lesson.id}/status?status=${status}`,
      {}
    ).subscribe({
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

  getAttendanceLabel(lesson: Lesson): string {
    if (lesson.status === 'CANCELLED' || lesson.status === 'MISSED') {
      return '—';
    }

    const summary = this.attendanceByLesson[lesson.id];
    if (!summary) {
      return '…';
    }

    const incomplete = summary.unmarked > 0 ? ' ·' : '';
    return `${summary.present} / ${summary.total}${incomplete}`;
  }

  getStatusLabel(status: LessonStatus): string {
    switch (status) {
      case LessonStatus.SCHEDULED:   return 'Scheduled';
      case LessonStatus.COMPLETED:   return 'Completed';
      case LessonStatus.CANCELLED:   return 'Cancelled';
      case LessonStatus.SUBSTITUTED: return 'Substituted';
      case LessonStatus.MISSED:      return 'Missed';
      default:                       return status;
    }
  }
}
