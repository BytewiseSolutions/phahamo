import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { forkJoin } from 'rxjs';
import { BackendService } from '../../../util/backend.service';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import { SuccessDialogService } from '../../../util/success-dialog.service';
import { Student } from '../../student/student';
import { SchoolClass } from '../class';

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
  classes: SchoolClass[] = [];
  loading = false;
  showAddPicker = false;
  selectedStudentIds: number[] = [];
  selectedForRemoval: number[] = [];
  transferringStudent: Student | null = null;
  errorMessage = '';

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService,
    private successDialog: SuccessDialogService
  ) {}

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
        fullLabel: s.schoolClassId
          ? `${s.firstName} ${s.lastName} (${s.admissionNumber}) — currently in another class`
          : `${s.firstName} ${s.lastName} (${s.admissionNumber})`
      }));
  }

  toggleAddPicker(): void {
    this.showAddPicker = !this.showAddPicker;
    this.errorMessage = '';
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

    const requests = this.selectedStudentIds.map(studentId =>
      this.backend.put(`/students/${studentId}/class`, {
        schoolClassId: this.schoolClassId
      })
    );

    forkJoin(requests).subscribe({
      next: () => {
        const count = this.selectedStudentIds.length;
        this.showAddPicker = false;
        this.selectedStudentIds = [];
        this.loadStudents();
        this.successDialog.show({
          message: `${count} student${count > 1 ? 's' : ''} added to class.`
        });
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

  onTransferClick(student: Student): void {
    this.transferringStudent = student;
    this.errorMessage = '';

    if (this.classes.length === 0) {
      this.backend.get<SchoolClass[]>('/classes').subscribe({
        next: (classes) => this.classes = classes,
        error: () => this.errorMessage = 'Failed to load classes.'
      });
    }
  }

  onTransferClosed(): void {
    this.transferringStudent = null;
  }

  async onTransferConfirmed(event: { studentId: number; schoolClassId: number }): Promise<void> {
    const targetClass = this.classes.find(c => c.id === event.schoolClassId);
    const student = this.transferringStudent;

    const confirmed = await this.confirmDialog.confirm({
      title: 'Transfer Student',
      message: `Transfer "${student?.firstName} ${student?.lastName}" to "${targetClass?.name ?? 'the selected class'}"?`,
      confirmText: 'Transfer'
    });

    if (!confirmed) return;

    this.errorMessage = '';

    this.backend.put(`/students/${event.studentId}/class`, {
      schoolClassId: event.schoolClassId
    }).subscribe({
      next: () => {
        this.transferringStudent = null;
        this.loadStudents();
        this.successDialog.show({ message: 'Student transferred.' });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to transfer student.';
      }
    });
  }

  async onRemove(student: Student): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Remove Student',
      message: `Remove "${student.firstName} ${student.lastName}" from this class?`,
      confirmText: 'Remove',
      danger: true
    });

    if (!confirmed) return;

    this.errorMessage = '';

    this.backend.put(`/students/${student.id}/class`, {
      schoolClassId: null
    }).subscribe({
      next: () => {
        this.loadStudents();
        this.successDialog.show({ message: 'Student removed from class.' });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to remove student.';
      }
    });
  }

  async onRemoveSelected(): Promise<void> {
    const count = this.selectedForRemoval.length;
    if (count === 0) return;

    const confirmed = await this.confirmDialog.confirm({
      title: 'Remove Students',
      message: `Remove ${count} student${count > 1 ? 's' : ''} from this class?`,
      confirmText: 'Remove',
      danger: true
    });

    if (!confirmed) return;

    this.errorMessage = '';

    const requests = this.selectedForRemoval.map(studentId =>
      this.backend.put(`/students/${studentId}/class`, {
        schoolClassId: null
      })
    );

    forkJoin(requests).subscribe({
      next: () => {
        this.loadStudents();
        this.successDialog.show({
          message: `${count} student${count > 1 ? 's' : ''} removed from class.`
        });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to remove one or more students.';
      }
    });
  }
}
