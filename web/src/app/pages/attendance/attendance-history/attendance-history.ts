import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import {
  LessonAttendanceSummary,
  StudentAttendanceReport,
  StudentAttendanceSummary
} from '../attendance-report';
import { SchoolClass } from '../../class/class';
import { Subject } from '../../subject/subject';
import { Term } from '../../terms/term';

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

  terms: Term[] = [];
  selectedTermId: number | null = null;

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
    const ninetyDaysAgo = new Date();
    ninetyDaysAgo.setDate(today.getDate() - 90);

    this.toDate = this.iso(today);
    this.fromDate = this.iso(ninetyDaysAgo);

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

            this.backend.get<Term[]>('/terms').subscribe({
              next: (terms) => {
                this.terms = terms;

                const today = this.iso(new Date());
                const current = terms.find(t =>
                  t.startDate.substring(0, 10) <= today &&
                  today <= t.endDate.substring(0, 10)
                );
                this.selectedTermId = (current ?? terms[0])?.id ?? null;

                this.loadingFilters = false;
                this.loadForMode();
              },
              error: () => {
                this.errorMessage = 'Failed to load terms.';
                this.loadingFilters = false;
              }
            });
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

  onTermChange(): void {
    if (this.selectedTermId === null) {
      this.loadForMode();
      return;
    }

    const term = this.terms.find(t => t.id === this.selectedTermId);
    if (!term) return;

    this.fromDate = term.startDate.substring(0, 10);
    this.toDate = term.endDate.substring(0, 10);

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

  downloadClassReportCards(): void {
    if (!this.selectedClassId || !this.selectedTermId) {
      this.errorMessage = 'Select a class and term first.';
      return;
    }

    const path = `/report-cards/class/${this.selectedClassId}?termId=${this.selectedTermId}`;

    this.backend.downloadBlob(path).subscribe({
      next: (blob) => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = `report-cards-class-${this.selectedClassId}-term-${this.selectedTermId}.zip`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(a.href);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to download class report cards.';
      }
    });
  }

  downloadStudentReportCard(studentId: number): void {
    if (!this.selectedTermId) {
      this.errorMessage = 'Select a term first.';
      return;
    }

    const path = `/report-cards/student/${studentId}?termId=${this.selectedTermId}`;

    this.backend.downloadBlob(path).subscribe({
      next: (blob) => {
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = `report-card-student-${studentId}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(a.href);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to download report card.';
      }
    });
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
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
