import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Teacher } from '../teacher';
import { EmploymentType } from '../employment-type';
import {SubjectClassAssignment} from '../../subject-class-assignment/subject-class-assignment';

@Component({
  selector: 'app-teacher-view',
  standalone: false,
  templateUrl: './teacher-view.html',
  styleUrl: './teacher-view.scss'
})
export class TeacherView implements OnChanges {

  @Input() teacher: Teacher | null = null;
  @Output() back = new EventEmitter<void>();

  activeTab: 'subjects' | 'timetable' | 'attendance' = 'subjects';

  assignments: SubjectClassAssignment[] = [];
  loadingAssignments = false;
  assignmentsError = '';

  constructor(
    private backend: BackendService
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['teacher'] && this.teacher) {
      this.loadAssignments();
    }
  }

  loadAssignments(): void {
    if (!this.teacher?.user?.id) {
      this.assignments = [];
      return;
    }

    this.loadingAssignments = true;
    this.assignmentsError = '';

    this.backend.get<SubjectClassAssignment[]>(
      `/subject-class-assignments/teacher/${this.teacher.user.id}`
    ).subscribe({
      next: (assignments) => {
        this.assignments = assignments;
        this.loadingAssignments = false;
      },
      error: (err) => {
        this.assignmentsError =
          err.error?.message || 'Failed to load teacher subjects.';
        this.loadingAssignments = false;
      }
    });
  }

  onTabChange(
    tab: 'subjects' | 'timetable' | 'attendance'
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
