import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Student } from '../../student/student';

@Component({
  selector: 'app-class-student-list',
  standalone: false,
  templateUrl: './class-student-list.html',
  styleUrl: './class-student-list.scss'
})
export class ClassStudentList implements OnChanges {

  @Input() schoolClassId!: number;

  students: Student[] = [];
  allStudents: Student[] = [];
  loading = false;
  showAddPicker = false;
  selectedStudentId: number | null = null;
  errorMessage = '';
  successMessage = '';

  constructor(private backend: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['schoolClassId'] && this.schoolClassId) {
      this.loadStudents();
    }
  }

  loadStudents(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<Student[]>(`/students/class/${this.schoolClassId}`).subscribe({
      next: (students) => {
        this.students = students;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load students.';
        this.loading = false;
      }
    });
  }

  get availableStudents(): Student[] {
    const assignedIds = new Set(this.students.map(s => s.id));
    return this.allStudents.filter(s => !assignedIds.has(s.id));
  }

  toggleAddPicker(): void {
    this.showAddPicker = !this.showAddPicker;
    this.errorMessage = '';
    this.successMessage = '';
    this.selectedStudentId = null;

    if (this.showAddPicker && this.allStudents.length === 0) {
      this.backend.get<Student[]>('/students').subscribe({
        next: (students) => this.allStudents = students,
        error: () => this.errorMessage = 'Failed to load students list.'
      });
    }
  }

  onAddStudent(): void {
    if (!this.selectedStudentId) return;

    this.errorMessage = '';
    this.successMessage = '';

    this.backend.put(`/students/${this.selectedStudentId}/class`, {
      schoolClassId: this.schoolClassId
    }).subscribe({
      next: () => {
        this.successMessage = 'Student added to class.';
        this.showAddPicker = false;
        this.selectedStudentId = null;
        this.loadStudents();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to add student.';
      }
    });
  }

  onRemove(student: Student): void {
    if (!confirm(`Remove "${student.firstName} ${student.lastName}" from this class?`)) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.backend.put(`/students/${student.id}/class`, {
      schoolClassId: null
    }).subscribe({
      next: () => {
        this.successMessage = 'Student removed from class.';
        this.loadStudents();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to remove student.';
      }
    });
  }
}
