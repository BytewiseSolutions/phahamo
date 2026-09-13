import { Component } from '@angular/core';
import { SuccessDialogService } from '../../util/success-dialog.service';

@Component({
  selector: 'app-success-dialog',
  standalone: false,
  templateUrl: './success-dialog.html',
  styleUrl: './success-dialog.scss',
})
export class SuccessDialog {

  state$;

  constructor(private successDialog: SuccessDialogService) {
    this.state$ = this.successDialog.state$;
  }

  onClose(): void {
    this.successDialog.respond();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }
}
