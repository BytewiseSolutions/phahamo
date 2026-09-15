import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { BackendService } from '../../../util/backend.service';
import { Parent } from '../parent';

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

  searchMode: 'email' | 'phone' = 'email';
  searching = false;
  errorMessage = '';
  items: Parent[] = [];
  selectedParent: Parent | null = null;

   input$ = new Subject<string>();

  constructor(private backend: BackendService) {
    this.input$.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap((query) => {
        if (!query || query.trim().length < 2) {
          return of([]);
        }
        this.searching = true;
        this.errorMessage = '';

        const param = this.searchMode === 'email'
          ? `email=${encodeURIComponent(query)}`
          : `phone=${encodeURIComponent(query)}`;

        return this.backend.get<Parent[]>(`/parents/search?${param}`).pipe(
          catchError(() => {
            this.errorMessage = 'Search failed.';
            return of([]);
          })
        );
      })
    ).subscribe((results) => {
      this.items = results;
      this.searching = false;
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible) {
      this.resetAll();
    }
  }

  setSearchMode(mode: 'email' | 'phone'): void {
    this.searchMode = mode;
    this.items = [];
    this.selectedParent = null;
    this.errorMessage = '';
  }

  onSearchInput(query: string): void {
    this.input$.next(query);
  }

  onSelect(parent: Parent): void {
    this.selectedParent = parent;
  }

  onClear(): void {
    this.selectedParent = null;
  }

  onConfirmLink(): void {
    if (!this.selectedParent) return;
    this.parentLinked.emit(this.selectedParent.id);
    this.resetAll();
  }

  onClose(): void {
    this.resetAll();
    this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }

  private resetAll(): void {
    this.items = [];
    this.selectedParent = null;
    this.errorMessage = '';
    this.searching = false;
  }
}
