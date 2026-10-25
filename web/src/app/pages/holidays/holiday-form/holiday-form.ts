import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Holiday, HolidayPayload } from '../holiday';

@Component({
  selector: 'app-holiday-form',
  standalone: false,
  templateUrl: './holiday-form.html',
  styleUrl: './holiday-form.scss'
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
  errorMessage = '';

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible) {
      this.reset();
      if (this.editing) {
        this.name = this.editing.name;
        this.startDate = this.editing.startDate;
        this.endDate = this.editing.endDate;
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
    if (!this.startDate || !this.endDate) {
      this.errorMessage = 'Start and end dates are required.';
      return;
    }
    if (this.endDate < this.startDate) {
      this.errorMessage = 'End date must be on or after start date.';
      return;
    }

    this.save.emit({
      id: this.editing?.id ?? null,
      payload: {
        name: this.name.trim(),
        startDate: this.startDate,
        endDate: this.endDate
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
    this.errorMessage = '';
  }

  private iso(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
