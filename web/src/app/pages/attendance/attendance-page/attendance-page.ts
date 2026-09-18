import { Component, OnInit } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { BackendService } from '../../../util/backend.service';
import { SchoolClass } from '../../class/class';
import { Attendance } from '../attendance';
import { AttendanceStatus } from '../attendance-status';

@Component({
  selector: 'app-attendance-page',
  standalone: false,
  templateUrl: './attendance-page.html',
  styleUrl: './attendance-page.scss'
})
export class AttendancePage implements OnInit {

  schoolClass: SchoolClass | null = null;
  loadingClass = true;
  noHomeroomClass = false;

  selectedDate: string = new Date().toISOString().substring(0, 10);
  students: Attendance[] = [];
  loadingStudents = false;

  errorMessage = '';

  showForm = false;
  editingRecord: Attendance | null = null;

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.loadMyClass();
  }

  loadMyClass(): void {
    this.loadingClass = true;

    this.backend.get<SchoolClass>('/classes/my-class').subscribe({
      next: (schoolClass) => {
        this.schoolClass = schoolClass;
        this.loadingClass = false;
        this.loadAttendance();
      },
      error: (err) => {
        if (err.status === 404) {
          this.noHomeroomClass = true;
        } else {
          this.errorMessage = err.error?.message || 'Failed to load your class.';
        }
        this.loadingClass = false;
      }
    });
  }

  loadAttendance(): void {
    if (!this.schoolClass) {
      return;
    }

    this.loadingStudents = true;
    this.errorMessage = '';

    const params = new HttpParams().set('date', this.selectedDate);

    this.backend.get<Attendance[]>(`/attendance/class/${this.schoolClass.id}`, params).subscribe({
      next: (records) => {
        this.students = records;
        this.loadingStudents = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load attendance.';
        this.loadingStudents = false;
      }
    });
  }

  onDateChange(): void {
    this.loadAttendance();
  }

  getStatusLabel(status: AttendanceStatus): string {
    switch (status) {
      case AttendanceStatus.PRESENT:
        return 'Present';
      case AttendanceStatus.ABSENT:
        return 'Absent';
      case AttendanceStatus.LATE:
        return 'Late';
      case AttendanceStatus.EXCUSED:
        return 'Excused';
      default:
        return status;
    }
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
    if (!this.schoolClass || !this.editingRecord) {
      return;
    }

    this.errorMessage = '';

    const studentId = this.editingRecord.studentId;
    const classId = this.schoolClass.id;
    const url = `/attendance/class/${classId}/student/${studentId}?date=${this.selectedDate}&status=${status}`;

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

  onMarkAllPresent(): void {
    if (!this.schoolClass) {
      return;
    }

    this.errorMessage = '';

    const records: Attendance[] = this.students.map(s => ({
      studentId: s.studentId,
      date: this.selectedDate,
      status: AttendanceStatus.PRESENT
    }));

    this.backend.postSilently<Attendance[]>(
      `/attendance/class/${this.schoolClass.id}?date=${this.selectedDate}`,
      records
    ).subscribe({
      next: () => {
        this.loadAttendance();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to mark attendance.';
      }
    });
  }
}
