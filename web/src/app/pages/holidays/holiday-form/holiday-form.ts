import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Holiday, HolidayPayload } from '../holiday';

@Component({
  selector: 'app-holiday-form',
  standalone: false,
  templateUrl: './holiday-form.html',
  styleUrls: ['./holiday-form.scss']
})
export class HolidayForm implements OnChanges {
  @Input() visible = false;
  @Input() editing: Holiday | null = null;
  @Input() saving = false;
  @Output() closed = new EventEmitter<void>();
  @Output() save = new EventEmitter<{ id: number | null; payload: HolidayPayload }>();

  name = '';
  startDate = '';
  endDate = '';
  isRange = false;
  recurring = false;
  errorMessage = '';

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible) {
      this.reset();
      if (this.editing) {
        this.name = this.editing.name;
        this.startDate = this.editing.startDate;
        this.endDate = this.editing.endDate;
        this.isRange = this.editing.startDate !== this.editing.endDate;
        this.recurring = this.editing.recurring;
      } else {
        this.startDate = this.iso(new Date());
        this.endDate = this.startDate;
      }
    }
  }

  onSubmit(): void {
    this.errorMessage = '';
    if (!this.name.trim()) {
      this.errorMessage = 'Holiday name is required.';
      return;
    }
    if (!this.startDate || (this.isRange && !this.endDate)) {
      this.errorMessage = this.isRange
        ? 'Start and end dates are required.'
        : 'Holiday date is required.';
      return;
    }
    if (this.isRange && this.endDate < this.startDate) {
      this.errorMessage = 'End date must be on or after start date.';
      return;
    }

    this.save.emit({
      id: this.editing?.id ?? null,
      payload: {
        name: this.name.trim(),
        startDate: this.startDate,
        endDate: this.isRange ? this.endDate : this.startDate,
        recurring: this.recurring
      }
    });
  }

  onClose(): void { this.closed.emit(); }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) this.onClose();
  }

  private reset(): void {
    this.name = '';
    this.startDate = '';
    this.endDate = '';
    this.isRange = false;
    this.recurring = false;
    this.errorMessage = '';
  }

  private iso(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
