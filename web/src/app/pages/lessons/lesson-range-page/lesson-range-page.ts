import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { SchoolClass } from '../../class/class';
import { Teacher } from '../../teacher/teacher';
import { Lesson } from '../lesson';

type Mode = 'class' | 'teacher';
type RangePreset = 'week' | 'month' | 'quarter' | 'custom';

@Component({
  selector: 'app-lesson-range-page',
  standalone: false,
  templateUrl: './lesson-range-page.html',
  styleUrl: './lesson-range-page.scss'
})
export class LessonRangePage implements OnInit {
  mode: Mode = 'class';
  preset: RangePreset = 'month';

  classes: SchoolClass[] = [];
  teachers: Teacher[] = [];

  selectedClassId: number | null = null;
  selectedTeacherId: number | null = null;

  startDate = '';
  endDate = '';

  lessons: Lesson[] = [];
  loading = false;
  errorMessage = '';

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.applyPreset('month');
    this.loadFilters();
  }

  loadFilters(): void {
    this.backend.get<SchoolClass[]>('/classes').subscribe({
      next: (classes) => {
        this.classes = classes;
        this.selectedClassId = classes[0]?.id ?? null;
        this.loadLessons();
      }
    });

    this.backend.get<Teacher[]>('/teachers').subscribe({
      next: (teachers) => {
        this.teachers = teachers;
        this.selectedTeacherId = teachers[0]?.id ?? null;
      }
    });
  }

  setMode(mode: Mode): void {
    this.mode = mode;
    this.lessons = [];
    this.errorMessage = '';
  }

  applyPreset(preset: RangePreset): void {
    this.preset = preset;
    const today = new Date();

    if (preset === 'week') {
      const day = today.getDay();
      const diffToMonday = day === 0 ? -6 : 1 - day;
      const monday = new Date(today);
      monday.setDate(today.getDate() + diffToMonday);
      const sunday = new Date(monday);
      sunday.setDate(monday.getDate() + 6);

      this.startDate = this.iso(monday);
      this.endDate = this.iso(sunday);
    } else if (preset === 'month') {
      const first = new Date(today.getFullYear(), today.getMonth(), 1);
      const last = new Date(today.getFullYear(), today.getMonth() + 1, 0);

      this.startDate = this.iso(first);
      this.endDate = this.iso(last);
    } else if (preset === 'quarter') {
      const quarterStartMonth = Math.floor(today.getMonth() / 3) * 3;
      const first = new Date(today.getFullYear(), quarterStartMonth, 1);
      const last = new Date(today.getFullYear(), quarterStartMonth + 3, 0);

      this.startDate = this.iso(first);
      this.endDate = this.iso(last);
    }

    if (preset !== 'custom') {
      this.loadLessons();
    }
  }

  loadLessons(): void {
    if (!this.startDate || !this.endDate) return;

    let url = '';
    if (this.mode === 'class') {
      if (!this.selectedClassId) {
        this.errorMessage = 'Please select a class.';
        return;
      }
      url = `/lessons/class/${this.selectedClassId}/range?startDate=${this.startDate}&endDate=${this.endDate}`;
    } else {
      if (!this.selectedTeacherId) {
        this.errorMessage = 'Please select a teacher.';
        return;
      }
      url = `/lessons/teacher/${this.selectedTeacherId}/range?startDate=${this.startDate}&endDate=${this.endDate}`;
    }

    this.loading = true;
    this.errorMessage = '';
    this.lessons = [];

    this.backend.get<Lesson[]>(url).subscribe({
      next: (res) => {
        this.lessons = res;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load lessons.';
        this.lessons = [];
        this.loading = false;
      }
    });
  }

  formatDate(value: string): string {
    if (!value) return '—';
    try {
      return new Date(value).toLocaleDateString('en-GB', {
        day: 'numeric', month: 'short', year: 'numeric', timeZone: 'UTC'
      });
    } catch {
      return value;
    }
  }

  statusClass(status: string): string {
    return status.toLowerCase();
  }

  private iso(d: Date): string {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
