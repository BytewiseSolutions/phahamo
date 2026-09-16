import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Teacher } from '../teacher';
import { EmploymentType } from '../employment-type';

@Component({
  selector: 'app-teacher-view',
  standalone: false,
  templateUrl: './teacher-view.html',
  styleUrl: './teacher-view.scss'
})
export class TeacherView {

  @Input() teacher: Teacher | null = null;
  @Output() back = new EventEmitter<void>();

  activeTab: 'subjects' | 'classes' | 'timetable' | 'attendance' = 'subjects';

  onTabChange(
    tab: 'subjects' | 'classes' | 'timetable' | 'attendance'
  ): void {
    this.activeTab = tab;
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
