import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { GradeBand } from '../grade-band';

@Component({
  selector: 'app-grade-band-form',
  standalone: false,
  templateUrl: './grade-band-form.html',
  styleUrl: './grade-band-form.scss'
})
export class GradeBandForm implements OnChanges {

  @Input() visible = false;
  @Input() editing: GradeBand | null = null;
  @Input() existingBands: GradeBand[] = [];

  @Output() closed = new EventEmitter<void>();
  @Output() save = new EventEmitter<GradeBand>();

  minScore = 0;
  maxScore = 100;
  grade = '';
  remark = '';
  errorMessage = '';

  get isEditMode(): boolean {
    return this.editing !== null && this.editing !== undefined;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible) {
      this.reset();
      if (this.editing) {
        this.minScore = this.editing.minScore;
        this.maxScore = this.editing.maxScore;
        this.grade = this.editing.grade;
        this.remark = this.editing.remark;
      }
    }
  }

  onSubmit(): void {
    if (!this.grade.trim()) {
      this.errorMessage = 'Grade is required.';
      return;
    }
    if (!this.remark.trim()) {
      this.errorMessage = 'Remark is required.';
      return;
    }
    if (this.minScore < 0 || this.minScore >= 100) {
      this.errorMessage = 'Min score must be between 0 and 99.';
      return;
    }
    if (this.maxScore <= this.minScore || this.maxScore > 100) {
      this.errorMessage = 'Max score must be greater than min and at most 100.';
      return;
    }

    const newBand: GradeBand = {
      id: this.editing?.id,
      minScore: this.minScore,
      maxScore: this.maxScore,
      grade: this.grade.trim().toUpperCase(),
      remark: this.remark.trim()
    };

    const clash = this.existingBands.find(b =>
      (!this.isEditMode || b.id !== this.editing?.id) &&
      b.grade.trim().toUpperCase() === newBand.grade
    );

    if (clash) {
      this.errorMessage = `Grade "${newBand.grade}" already exists.`;
      return;
    }

    this.save.emit(newBand);
  }

  onClose(): void {
    this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) this.onClose();
  }

  private reset(): void {
    this.minScore = 0;
    this.maxScore = 100;
    this.grade = '';
    this.remark = '';
    this.errorMessage = '';
  }
}
