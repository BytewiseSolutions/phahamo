import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface SuccessOptions {
  title?: string;
  message: string;
  buttonText?: string;
}

interface SuccessState extends SuccessOptions {
  resolve: () => void;
}

@Injectable({
  providedIn: 'root'
})
export class SuccessDialogService {

  private stateSubject = new BehaviorSubject<SuccessState | null>(null);
  state$ = this.stateSubject.asObservable();

  show(options: SuccessOptions): Promise<void> {
    return new Promise((resolve) => {
      this.stateSubject.next({
        title: options.title ?? 'Success',
        message: options.message,
        buttonText: options.buttonText ?? 'OK',
        resolve
      });
    });
  }

  respond(): void {
    const current = this.stateSubject.value;

    if (current) {
      current.resolve();
      this.stateSubject.next(null);
    }
  }
}
