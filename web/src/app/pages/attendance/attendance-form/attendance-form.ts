import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Attendance } from '../attendance';
import { AttendanceStatus } from '../attendance-status';

@Component({
  selector: 'app-attendance-form',
  standalone: false,
  templateUrl: './attendance-form.html',
  styleUrl: './attendance-form.scss'
})
export class AttendanceForm implements OnChanges {

  @Input() visible = false;
  @Input() record: Attendance | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<AttendanceStatus>();

  selectedStatus: AttendanceStatus = AttendanceStatus.PRESENT;
  statuses = Object.values(AttendanceStatus);

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['record'] && this.record) {
      this.selectedStatus = this.record.status || AttendanceStatus.PRESENT;
    }
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

  onSave(): void {
    this.saved.emit(this.selectedStatus);
  }

  onClose(): void {
    this.closed.emit();
  }
}
