import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { forkJoin } from 'rxjs';
import { BackendService } from '../../../util/backend.service';
import { SchoolClass } from '../../class/class';
import { Term } from '../../terms/term';
import { FeeItem } from '../fee-item';
import { FeeStructure } from '../fee-structure';
import { FeeStructurePayload } from '../fee-structure-payload';

@Component({
  selector: 'app-fee-structure-form',
  standalone: false,
  templateUrl: './fee-structure-form.html',
  styleUrl: './fee-structure-form.scss'
})
export class FeeStructureForm implements OnChanges {

  @Input() visible = false;
  @Input() editingStructure: FeeStructure | null = null;
  @Input() classes: SchoolClass[] = [];
  @Input() terms: Term[] = [];
  @Input() defaultClassId: number | null = null;
  @Input() defaultTermId: number | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  selectedClassIds = new Set<number>();
  selectedTermId: number | null = null;
  items: FeeItem[] = [];

  saving = false;
  errorMessage = '';

  constructor(private backend: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible']?.currentValue === true) {
      this.reset();
    }
  }

  private reset(): void {
    this.errorMessage = '';
    this.saving = false;
    this.selectedClassIds.clear();

    if (this.editingStructure) {
      this.selectedClassIds.add(this.editingStructure.classId);
      this.selectedTermId = this.editingStructure.termId;
      this.items = this.editingStructure.items.map(i => ({ ...i }));
    } else {
      if (this.defaultClassId) {
        this.selectedClassIds.add(this.defaultClassId);
      }
      this.selectedTermId = this.defaultTermId;
      this.items = [
        { name: 'School Fees', amount: 0, mandatory: true }
      ];
    }
  }

  get isEditing(): boolean {
    return this.editingStructure != null;
  }

  get runningTotal(): number {
    return this.items.reduce((sum, i) => sum + (i.amount || 0), 0);
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

  addItem(): void {
    this.items.push({ name: '', amount: 0, mandatory: true });
  }

  removeItem(index: number): void {
    this.items.splice(index, 1);
  }

  onSave(): void {
    this.errorMessage = '';

    if (this.selectedClassIds.size === 0) {
      this.errorMessage = 'Select at least one class.';
      return;
    }
    if (!this.selectedTermId) {
      this.errorMessage = 'Term is required.';
      return;
    }
    if (this.items.length === 0) {
      this.errorMessage = 'At least one fee item is required.';
      return;
    }
    for (const item of this.items) {
      if (!item.name || item.name.trim() === '') {
        this.errorMessage = 'Every fee item must have a name.';
        return;
      }
      if (!item.amount || item.amount <= 0) {
        this.errorMessage = 'Every fee item must have an amount greater than 0.';
        return;
      }
    }

    // Edit mode — single class, single PUT
    if (this.isEditing) {
      const payload: FeeStructurePayload = {
        classId: this.editingStructure!.classId,
        termId: this.selectedTermId,
        items: this.items
      };

      this.saving = true;
      this.backend.putSilently<FeeStructure>(`/fee-structures/${this.editingStructure!.id}`, payload).subscribe({
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

    // Create mode — N POSTs, one per selected class
    const classIds = Array.from(this.selectedClassIds);
    this.saving = true;

    const requests = classIds.map(classId => {
      const payload: FeeStructurePayload = {
        classId,
        termId: this.selectedTermId!,
        items: this.items
      };
      return this.backend.postSilently<FeeStructure>('/fee-structures', payload);
    });

    forkJoin(requests).subscribe({
      next: () => {
        this.saving = false;
        this.saved.emit();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Some fee structures failed to create.';
        this.saving = false;
      }
    });
  }

  onClose(): void {
    if (this.saving) return;
    this.closed.emit();
  }
}
