import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-parent-link-form',
  standalone: false,
  templateUrl: './parent-link-form.html',
  styleUrl: './parent-link-form.scss'
})
export class ParentLinkForm implements OnChanges {

  @Input() visible = false;

  @Output() closed = new EventEmitter<void>();
  @Output() parentLinked = new EventEmitter<number>();

  linkForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.linkForm = this.fb.group({
      parentId: [null, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible) {
      this.resetForm();
    }
  }

  onSubmit(): void {
    if (this.linkForm.invalid) {
      this.linkForm.markAllAsTouched();
      return;
    }

    this.parentLinked.emit(this.linkForm.value.parentId);
    this.resetForm();
  }

  onClose(): void {
    this.resetForm();
    this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }

  private resetForm(): void {
    this.linkForm.reset({ parentId: null });
  }
}
