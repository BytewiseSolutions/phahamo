import { Component, Input, OnChanges, OnDestroy, SimpleChanges } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Subject, forkJoin, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, catchError } from 'rxjs/operators';
import { BackendService } from '../../../util/backend.service';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import { SuccessDialogService } from '../../../util/success-dialog.service';
import { Page } from '../../../util/page';
import { Student } from '../../student/student';
import { SchoolClass } from '../class';
import { StudentClassFeePreview } from '../student-class-fee-preview';

interface StudentOption extends Student {
  fullLabel: string;
}

@Component({
  selector: 'app-class-student-list',
  standalone: false,
  templateUrl: './class-student-list.html',
  styleUrl: './class-student-list.scss'
})
export class ClassStudentList implements OnChanges, OnDestroy {

  @Input() schoolClassId!: number;

  students: Student[] = [];
  availableStudents: StudentOption[] = [];
  searchLoading = false;
  classes: SchoolClass[] = [];
  loading = false;
  showAddPicker = false;
  selectedStudentIds: number[] = [];
  selectedForRemoval: number[] = [];
  transferringStudent: Student | null = null;
  errorMessage = '';

  typeahead$ = new Subject<string>();

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService,
    private successDialog: SuccessDialogService
  ) {
    this.typeahead$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => {
        this.searchLoading = true;

        let params = new HttpParams().set('size', 20);
        if (term) {
          params = params.set('search', term);
        }

        return this.backend.get<Page<Student>>('/students', params).pipe(
          catchError(() => {
            this.errorMessage = 'Failed to search students.';
            return of<Page<Student>>({ content: [], page: { size: 20, number: 0, totalElements: 0, totalPages: 0 } });
          })
        );
      })
    ).subscribe(result => {
      this.searchLoading = false;
      this.recomputeAvailableStudents(result.content);
    });
  }

  ngOnDestroy(): void {
    this.typeahead$.complete();
  }

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
      },
      error: () => {
        this.errorMessage = 'Failed to load students.';
        this.loading = false;
      }
    });
  }

  private recomputeAvailableStudents(searchResults: Student[]): void {
    const assignedIds = new Set(this.students.map(s => s.id));
    this.availableStudents = searchResults
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

    if (this.showAddPicker) {
      this.typeahead$.next('');
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

    this.errorMessage = '';
    this.backend.get<StudentClassFeePreview>(
      `/students/${event.studentId}/class-fee-preview?schoolClassId=${event.schoolClassId}`
    ).subscribe({
      next: (preview) => this.confirmTransfer(event, preview, student, targetClass),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to preview the fee change.';
      }
    });
  }

  private async confirmTransfer(
    event: { studentId: number; schoolClassId: number },
    preview: StudentClassFeePreview,
    student: Student | null,
    targetClass: SchoolClass | undefined
  ): Promise<void> {
    let message = `Transfer "${student?.firstName} ${student?.lastName}" to "${targetClass?.name ?? 'the selected class'}"?`;

    if (preview.feesChanged) {
      const changes = preview.terms
        .filter(line => line.willUpdate)
        .map(line => `${line.termName}: fees ${this.formatAmount(line.oldTotalFees)} → ${this.formatAmount(line.newTotalFees)}, balance ${this.formatAmount(line.oldBalance)} → ${this.formatAmount(line.newBalance)}`)
        .join('\n');
      message += `\n\nUnpaid fees will be replaced with the new class fees. Paid amounts stay unchanged.\n\n${changes}`;
    } else {
      message += '\n\nNo unpaid fee amounts will change.';
    }

    const confirmed = await this.confirmDialog.confirm({
      title: 'Transfer Student',
      message,
      confirmText: 'Transfer'
    });

    if (!confirmed) return;

    this.backend.put(`/students/${event.studentId}/class`, {
      schoolClassId: event.schoolClassId,
      confirmFeeChange: true
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

  private formatAmount(value: number): string {
    return `M ${value.toFixed(2)}`;
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

  async onReactivate(student: Student): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Reactivate Student',
      message: `Reactivate "${student.firstName} ${student.lastName}" as an active student?`,
      confirmText: 'Reactivate'
    });

    if (!confirmed) return;

    this.errorMessage = '';

    this.backend.put(`/students/${student.id}/reactivate`, {}).subscribe({
      next: () => {
        this.loadStudents();
        this.successDialog.show({ message: 'Student reactivated.' });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to reactivate student.';
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
