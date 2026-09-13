import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface ConfirmOptions {
  title?: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  danger?: boolean;
}

interface ConfirmState extends ConfirmOptions {
  resolve: (result: boolean) => void;
}

@Injectable({
  providedIn: 'root'
})
export class ConfirmDialogService {

  private stateSubject = new BehaviorSubject<ConfirmState | null>(null);
  state$ = this.stateSubject.asObservable();

  confirm(options: ConfirmOptions): Promise<boolean> {
    return new Promise((resolve) => {
      this.stateSubject.next({
        title: options.title ?? 'Confirm',
        message: options.message,
        confirmText: options.confirmText ?? 'Confirm',
        cancelText: options.cancelText ?? 'Cancel',
        danger: options.danger ?? false,
        resolve
      });
    });
  }

  respond(result: boolean): void {
    const current = this.stateSubject.value;
    if (current) {
      current.resolve(result);
      this.stateSubject.next(null);
    }
  }
}
