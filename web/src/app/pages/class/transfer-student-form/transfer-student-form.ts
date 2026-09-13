import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Student } from '../../student/student';
import { SchoolClass } from '../class';

@Component({
  selector: 'app-transfer-student-form',
  standalone: false,
  templateUrl: './transfer-student-form.html',
  styleUrl: './transfer-student-form.scss'
})
export class TransferStudentForm {

  @Input() visible = false;
  @Input() student: Student | null = null;
  @Input() classes: SchoolClass[] = [];

  @Output() closed = new EventEmitter<void>();
  @Output() transferred = new EventEmitter<{ studentId: number; schoolClassId: number }>();

  targetClassId: number | null = null;

  get transferableClasses(): SchoolClass[] {
    return this.classes.filter(c => c.id !== this.student?.schoolClassId);
  }

  onSubmit(): void {
    if (!this.student || !this.targetClassId) return;

    this.transferred.emit({
      studentId: this.student.id,
      schoolClassId: this.targetClassId
    });

    this.targetClassId = null;
  }

  onClose(): void {
    this.targetClassId = null;
    this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }
}
