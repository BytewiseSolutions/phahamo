import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { SchoolClass } from '../../class/class';
import { AnnualFee } from '../annual-fee';
import {AcademicYear} from '../../academic-years/academic-year';
import {AnnualFeePayload} from '../annual-fee-payload';


@Component({
  selector: 'app-annual-fee-form',
  standalone: false,
  templateUrl: './annual-fee-form.html',
  styleUrl: './annual-fee-form.scss'
})
export class AnnualFeeForm implements OnChanges {

  @Input() visible = false;
  @Input() editingAnnualFee: AnnualFee | null = null;
  @Input() classes: SchoolClass[] = [];
  @Input() academicYears: AcademicYear[] = [];
  @Input() defaultClassId: number | null = null;
  @Input() defaultAcademicYearId: number | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  selectedClassId: number | null = null;
  selectedAcademicYearId: number | null = null;
  name = '';
  amount: number | null = null;
  description = '';

  selectedClassIds = new Set<number>();

  saving = false;
  errorMessage = '';

  constructor(private backend: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible']?.currentValue === true) {
      this.reset();
    }
  }
  isClassSelected(classId: number): boolean {
    return this.selectedClassIds.has(classId);
  }

  toggleClass(classId: number): void {
    if (this.selectedClassIds.has(classId)) {
      this.selectedClassIds.delete(classId);
    } else {
      this.selectedClassIds.add(classId);
    }
  }

  selectAllClasses(): void {
    this.classes.forEach(c => this.selectedClassIds.add(c.id));
  }

  clearClasses(): void {
    this.selectedClassIds.clear();
  }
  private reset(): void {
    this.errorMessage = '';
    this.saving = false;
    this.selectedClassIds.clear();

    if (this.editingAnnualFee) {
      this.selectedClassIds.add(this.editingAnnualFee.classId);
      this.selectedAcademicYearId = this.editingAnnualFee.academicYearId;
      this.name = this.editingAnnualFee.name;
      this.amount = this.editingAnnualFee.amount;
      this.description = this.editingAnnualFee.description ?? '';
    } else {
      if (this.defaultClassId) {
        this.selectedClassIds.add(this.defaultClassId);
      }
      this.selectedAcademicYearId = this.defaultAcademicYearId;
      this.name = '';
      this.amount = null;
      this.description = '';
    }
  }

  get isEditing(): boolean {
    return this.editingAnnualFee != null;
  }

  onSave(): void {
    if (this.selectedClassIds.size === 0) {
      this.errorMessage = 'Select at least one class.';
      return;
    }
    if (!this.selectedAcademicYearId) {
      this.errorMessage = 'Academic year is required.';
      return;
    }
    if (!this.name || this.name.trim() === '') {
      this.errorMessage = 'Name is required.';
      return;
    }
    if (this.amount === null || this.amount <= 0) {
      this.errorMessage = 'Amount must be greater than 0.';
      return;
    }

    if (this.isEditing) {
      const payload: AnnualFeePayload = {
        classId: this.editingAnnualFee!.classId,
        academicYearId: this.selectedAcademicYearId,
        name: this.name.trim(),
        amount: this.amount,
        description: this.description?.trim() || undefined
      };

      this.saving = true;
      this.backend.putSilently<AnnualFee>(`/annual-fees/${this.editingAnnualFee!.id}`, payload).subscribe({
        next: () => {
          this.saving = false;
          this.saved.emit();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to save.';
          this.saving = false;
        }
      });
      return;
    }

    const classIds = Array.from(this.selectedClassIds);
    this.saving = true;

    const requests = classIds.map(classId =>
      this.backend.postSilently<AnnualFee>('/annual-fees', {
        classId,
        academicYearId: this.selectedAcademicYearId,
        name: this.name.trim(),
        amount: this.amount,
        description: this.description?.trim() || undefined
      })
    );

    import('rxjs').then(({ forkJoin }) => {
      forkJoin(requests).subscribe({
        next: () => {
          this.saving = false;
          this.saved.emit();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Some fees failed to create.';
          this.saving = false;
        }
      });
    });
  }

  onClose(): void {
    if (this.saving) return;
    this.closed.emit();
  }
}
