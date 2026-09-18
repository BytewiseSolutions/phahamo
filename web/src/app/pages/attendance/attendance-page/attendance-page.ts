import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { Attendance } from '../attendance';
import { AttendanceStatus } from '../attendance-status';
import { Lesson } from '../../timetable/lesson';

@Component({
  selector: 'app-attendance-page',
  standalone: false,
  templateUrl: './attendance-page.html',
  styleUrl: './attendance-page.scss'
})
export class AttendancePage implements OnInit {

  lesson: Lesson | null = null;
  lessonId: number | null = null;
  assignmentId: number | null = null;

  students: Attendance[] = [];
  selectedIds = new Set<number>();
  loading = false;
  errorMessage = '';

  showForm = false;
  editingRecord: Attendance | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private backend: BackendService
  ) {}

  ngOnInit(): void {
    this.lessonId = Number(this.route.snapshot.paramMap.get('lessonId'));
    this.assignmentId = Number(this.route.snapshot.paramMap.get('assignmentId'));
    this.loadLesson();
  }

  loadLesson(): void {
    this.backend.get<Lesson>(`/lessons/${this.lessonId}`).subscribe({
      next: (lesson) => {
        this.lesson = lesson;
        this.loadAttendance();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load lesson.';
      }
    });
  }

  loadAttendance(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<Attendance[]>(`/attendance/lesson/${this.lessonId}`).subscribe({
      next: (records) => {
        this.students = records;
        this.selectedIds.clear();
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load attendance.';
        this.loading = false;
      }
    });
  }

  onEditAttendance(record: Attendance): void {
    this.editingRecord = record;
    this.showForm = true;
    this.errorMessage = '';
  }

  onFormClosed(): void {
    this.showForm = false;
    this.editingRecord = null;
  }

  onAttendanceSaved(status: AttendanceStatus): void {
    if (!this.editingRecord) return;

    this.errorMessage = '';
    const studentId = this.editingRecord.studentId;
    const url = `/attendance/lesson/${this.lessonId}/student/${studentId}?status=${status}`;

    this.backend.patch<Attendance>(url, {}).subscribe({
      next: () => {
        this.showForm = false;
        this.editingRecord = null;
        this.loadAttendance();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to save attendance.';
      }
    });
  }

  get allSelected(): boolean {
    return this.students.length > 0 && this.selectedIds.size === this.students.length;
  }

  get someSelected(): boolean {
    return this.selectedIds.size > 0;
  }

  isSelected(studentId: number): boolean {
    return this.selectedIds.has(studentId);
  }

  toggleSelect(studentId: number): void {
    if (this.selectedIds.has(studentId)) {
      this.selectedIds.delete(studentId);
    } else {
      this.selectedIds.add(studentId);
    }
  }

  toggleSelectAll(): void {
    if (this.allSelected) {
      this.selectedIds.clear();
    } else {
      this.students.forEach(s => this.selectedIds.add(s.studentId));
    }
  }

  onMarkSelectedPresent(): void {
    this.errorMessage = '';

    const records: Attendance[] = this.students
      .filter(s => this.selectedIds.has(s.studentId))
      .map(s => ({
        studentId: s.studentId,
        lessonId: this.lessonId!,
        date: this.lesson!.date,
        status: AttendanceStatus.PRESENT
      }));

    this.backend.postSilently<Attendance[]>(`/attendance/lesson/${this.lessonId}`, records).subscribe({
      next: () => this.loadAttendance(),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to mark attendance.';
      }
    });
  }

  getStatusLabel(status: AttendanceStatus): string {
    switch (status) {
      case AttendanceStatus.PRESENT: return 'Present';
      case AttendanceStatus.ABSENT: return 'Absent';
      case AttendanceStatus.LATE: return 'Late';
      case AttendanceStatus.EXCUSED: return 'Excused';
      default: return status;
    }
  }

  onBack(): void {
    this.router.navigate(['/my-classes', this.assignmentId]);
  }
}
