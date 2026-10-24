import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Lesson } from '../lesson';
import { Teacher } from '../../teacher/teacher';

@Component({
  selector: 'app-lesson-substitution-dialog',
  standalone: false,
  templateUrl: './lesson-substitution-dialog.html',
  styleUrl: './lesson-substitution-dialog.scss'
})
export class LessonSubstitutionDialog implements OnChanges {
  @Input() visible = false;
  @Input() lesson: Lesson | null = null;
  @Input() teachers: Teacher[] = [];
  @Input() loading = false;
  @Input() errorMessage = '';

  @Output() closed = new EventEmitter<void>();
  @Output() confirmed = new EventEmitter<number>();

  selectedTeacherUserId: number | null = null;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible']?.currentValue === true) {
      this.selectedTeacherUserId = null;
    }
  }

  confirm(): void {
    if (this.selectedTeacherUserId != null) {
      this.confirmed.emit(this.selectedTeacherUserId);
    }
  }
}
