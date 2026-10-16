import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { TermResult } from '../term-result';

@Component({
  selector: 'app-result-override-form',
  standalone: false,
  templateUrl: './result-override-form.html',
  styleUrl: './result-override-form.scss'
})
export class ResultOverrideForm implements OnChanges {

  @Input() result: TermResult | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  newMark = 0;
  reason = '';
  saving = false;
  errorMessage = '';

  constructor(private backend: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['result'] && this.result) {
      this.newMark = this.result.finalMark;
      this.reason = '';
      this.errorMessage = '';
      this.saving = false;
    }
  }

  onSubmit(): void {
    if (!this.result) return;

    if (this.newMark < 0 || this.newMark > 100) {
      this.errorMessage = 'Mark must be between 0 and 100.';
      return;
    }
    if (!this.reason.trim()) {
      this.errorMessage = 'Reason is required.';
      return;
    }

    this.saving = true;
    this.errorMessage = '';

    const url = `/term-results/${this.result.id}/override?mark=${this.newMark}&reason=${encodeURIComponent(this.reason.trim())}`;

    this.backend.postSilently<TermResult>(url, {}).subscribe({
      next: () => {
        this.saving = false;
        this.saved.emit();
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'Failed to override mark.';
      }
    });
  }

  onClose(): void {
    this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) this.onClose();
  }
}
