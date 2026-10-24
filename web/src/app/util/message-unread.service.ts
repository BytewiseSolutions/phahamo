import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { BackendService } from './backend.service';

@Injectable({ providedIn: 'root' })
export class MessageUnreadService {
  private readonly unreadSubject = new BehaviorSubject<number>(0);
  readonly unread$ = this.unreadSubject.asObservable();

  constructor(private backend: BackendService) {}

  refresh(): void {
    this.backend.get<number>('/messages/unread-count').subscribe({
      next: (count) => this.unreadSubject.next(count),
      error: () => this.unreadSubject.next(0)
    });
  }
}
