import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import {
  LessonAttendanceSummary,
  StudentAttendanceReport,
  StudentAttendanceSummary
} from '../attendance-report';
import { SchoolClass } from '../../class/class';
import { Subject } from '../../subject/subject';

type Mode = 'byLesson' | 'byStudent';

@Component({
  selector: 'app-attendance-history',
  standalone: false,
  templateUrl: './attendance-history.html',
  styleUrl: './attendance-history.scss'
})
export class AttendanceHistory implements OnInit {

  mode: Mode = 'byLesson';

  classes: SchoolClass[] = [];
  subjects: Subject[] = [];

  selectedClassId: number | null = null;
  selectedSubjectId: number | null = null;
  selectedStudentId: number | null = null;
  fromDate = '';
  toDate = '';

  lessonRows: LessonAttendanceSummary[] = [];
  classRows: StudentAttendanceSummary[] = [];
  studentReport: StudentAttendanceReport | null = null;

  loadingFilters = false;
  loading = false;
  errorMessage = '';

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    const today = new Date();
    const firstOfYear = new Date(today.getFullYear(), 0, 1);

    this.toDate = this.iso(today);
    this.fromDate = this.iso(firstOfYear);

    this.loadFilters();
  }

  loadFilters(): void {
    this.loadingFilters = true;

    this.backend.get<SchoolClass[]>('/classes').subscribe({
      next: (classes) => {
        this.classes = classes;
        this.selectedClassId = classes[0]?.id ?? null;

        this.backend.get<Subject[]>('/subjects').subscribe({
          next: (subjects) => {
            this.subjects = subjects;
            this.loadingFilters = false;
            this.loadForMode();
          },
          error: () => {
            this.errorMessage = 'Failed to load subjects.';
            this.loadingFilters = false;
          }
        });
      },
      error: () => {
        this.errorMessage = 'Failed to load classes.';
        this.loadingFilters = false;
      }
    });
  }

  setMode(mode: Mode): void {
    this.mode = mode;
    this.errorMessage = '';
    this.loadForMode();
  }

  loadForMode(): void {
    if (this.mode === 'byLesson') {
      this.loadLessons();
    } else {
      this.loadClassBreakdown();
    }
  }

  loadLessons(): void {
    if (!this.selectedClassId || !this.fromDate || !this.toDate) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    let url = `/attendance/report/lessons?classId=${this.selectedClassId}`
      + `&from=${this.fromDate}&to=${this.toDate}`;

    if (this.selectedSubjectId) {
      url += `&subjectId=${this.selectedSubjectId}`;
    }

    this.backend.get<LessonAttendanceSummary[]>(url).subscribe({
      next: (rows) => {
        this.lessonRows = rows;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load lesson attendance.';
        this.lessonRows = [];
        this.loading = false;
      }
    });
  }

  loadClassBreakdown(): void {
    if (!this.selectedClassId || !this.fromDate || !this.toDate) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    let url = `/attendance/report/class/${this.selectedClassId}`
      + `?from=${this.fromDate}&to=${this.toDate}`;

    if (this.selectedSubjectId) {
      url += `&subjectId=${this.selectedSubjectId}`;
    }

    this.backend.get<StudentAttendanceSummary[]>(url).subscribe({
      next: (rows) => {
        this.classRows = rows;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load class attendance.';
        this.classRows = [];
        this.loading = false;
      }
    });
  }

  openStudent(studentId: number): void {
    this.selectedStudentId = studentId;
    this.loading = true;
    this.errorMessage = '';

    let url = `/attendance/report/student/${studentId}`
      + `?from=${this.fromDate}&to=${this.toDate}`;

    if (this.selectedClassId) url += `&classId=${this.selectedClassId}`;
    if (this.selectedSubjectId) url += `&subjectId=${this.selectedSubjectId}`;

    this.backend.get<StudentAttendanceReport>(url).subscribe({
      next: (report) => {
        this.studentReport = report;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load student attendance.';
        this.studentReport = null;
        this.loading = false;
      }
    });
  }

  closeStudentReport(): void {
    this.studentReport = null;
    this.selectedStudentId = null;
  }

  exportCsv(): void {
    const rows = this.mode === 'byLesson' ? this.lessonRows : this.classRows;

    if (rows.length === 0) {
      this.errorMessage = 'Nothing to export.';
      return;
    }

    const headers = this.mode === 'byLesson'
      ? ['Date', 'Period', 'Subject', 'Class', 'Present', 'Absent', 'Late', 'Excused', 'Total', 'Present %']
      : ['Admission #', 'First Name', 'Last Name', 'Present', 'Absent', 'Late', 'Excused', 'Total', 'Present %'];

    const csv = [headers.join(',')];

    for (const row of rows as any[]) {
      if (this.mode === 'byLesson') {
        csv.push([
          row.date,
          row.periodName,
          row.subjectName,
          row.className,
          row.present,
          row.absent,
          row.late,
          row.excused,
          row.total,
          row.presentPercent
        ].map(this.csvEscape).join(','));
      } else {
        csv.push([
          row.admissionNumber,
          row.firstName,
          row.lastName,
          row.present,
          row.absent,
          row.late,
          row.excused,
          row.total,
          row.presentPercent
        ].map(this.csvEscape).join(','));
      }
    }

    const blob = new Blob([csv.join('\n')], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `attendance-${this.mode}-${this.fromDate}-to-${this.toDate}.csv`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }

  private csvEscape(value: any): string {
    if (value === null || value === undefined) return '';
    const s = String(value);
    if (s.includes(',') || s.includes('"') || s.includes('\n')) {
      return `"${s.replace(/"/g, '""')}"`;
    }
    return s;
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

  formatPercent(value: number): string {
    return `${value.toFixed(1)}%`;
  }

  statusLabel(status: string): string {
    return status.charAt(0) + status.slice(1).toLowerCase();
  }

  private iso(d: Date): string {
    return d.toISOString().substring(0, 10);
  }
}
