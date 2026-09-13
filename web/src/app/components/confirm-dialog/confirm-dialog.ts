import { Component } from '@angular/core';
import { ConfirmDialogService } from '../../util/confirm-dialog.service';

@Component({
  selector: 'app-confirm-dialog',
  standalone: false,
  templateUrl: './confirm-dialog.html',
  styleUrl: './confirm-dialog.scss',
})
export class ConfirmDialog {

  state$;

  constructor(private confirmDialog: ConfirmDialogService) {
    this.state$ = this.confirmDialog.state$;
  }

  onConfirm(): void {
    this.confirmDialog.respond(true);
  }

  onCancel(): void {
    this.confirmDialog.respond(false);
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onCancel();
    }
  }
}
