import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { forkJoin } from 'rxjs';
import { BackendService } from '../../../util/backend.service';
import { Student } from '../../student/student';

interface StudentOption extends Student {
  fullLabel: string;
}

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
  availableStudents: StudentOption[] = [];
  loading = false;
  showAddPicker = false;
  selectedStudentIds: number[] = [];
  selectedForRemoval: number[] = [];
  errorMessage = '';
  successMessage = '';

  constructor(private backend: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['schoolClassId'] && this.schoolClassId) {
      this.loadStudents();
    }
  }

  get allSelected(): boolean {
    return this.students.length > 0 && this.selectedForRemoval.length === this.students.length;
  }

  loadStudents(): void {
    this.loading = true;
    this.errorMessage = '';
    this.selectedForRemoval = [];

    this.backend.get<Student[]>(`/students/class/${this.schoolClassId}`).subscribe({
      next: (students) => {
        this.students = students;
        this.loading = false;
        this.recomputeAvailableStudents();
      },
      error: () => {
        this.errorMessage = 'Failed to load students.';
        this.loading = false;
      }
    });
  }

  private recomputeAvailableStudents(): void {
    const assignedIds = new Set(this.students.map(s => s.id));
    this.availableStudents = this.allStudents
      .filter(s => !assignedIds.has(s.id))
      .map(s => ({
        ...s,
        fullLabel: `${s.firstName} ${s.lastName} (${s.admissionNumber})`
      }));
  }

  toggleAddPicker(): void {
    this.showAddPicker = !this.showAddPicker;
    this.errorMessage = '';
    this.successMessage = '';
    this.selectedStudentIds = [];

    if (this.showAddPicker && this.allStudents.length === 0) {
      this.backend.get<Student[]>('/students').subscribe({
        next: (students) => {
          this.allStudents = students;
          this.recomputeAvailableStudents();
        },
        error: () => this.errorMessage = 'Failed to load students list.'
      });
    }
  }

  onAddStudents(): void {
    if (this.selectedStudentIds.length === 0) return;

    this.errorMessage = '';
    this.successMessage = '';

    const requests = this.selectedStudentIds.map(studentId =>
      this.backend.put(`/students/${studentId}/class`, {
        schoolClassId: this.schoolClassId
      })
    );

    forkJoin(requests).subscribe({
      next: () => {
        const count = this.selectedStudentIds.length;
        this.successMessage = `${count} student${count > 1 ? 's' : ''} added to class.`;
        this.showAddPicker = false;
        this.selectedStudentIds = [];
        this.loadStudents();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to add one or more students.';
      }
    });
  }

  onToggleSelect(studentId: number): void {
    const index = this.selectedForRemoval.indexOf(studentId);
    if (index === -1) {
      this.selectedForRemoval.push(studentId);
    } else {
      this.selectedForRemoval.splice(index, 1);
    }
  }

  onToggleSelectAll(event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
    this.selectedForRemoval = checked ? this.students.map(s => s.id) : [];
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

  onRemoveSelected(): void {
    const count = this.selectedForRemoval.length;
    if (count === 0) return;

    if (!confirm(`Remove ${count} student${count > 1 ? 's' : ''} from this class?`)) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    const requests = this.selectedForRemoval.map(studentId =>
      this.backend.put(`/students/${studentId}/class`, {
        schoolClassId: null
      })
    );

    forkJoin(requests).subscribe({
      next: () => {
        this.successMessage = `${count} student${count > 1 ? 's' : ''} removed from class.`;
        this.loadStudents();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to remove one or more students.';
      }
    });
  }
}
