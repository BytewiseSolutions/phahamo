import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Student } from '../student';

interface StatusChange {
  id: number;
  previousStatus: string;
  newStatus: string;
  reason: string;
  createdAt: string;
}

@Component({
  selector: 'app-student-view',
  standalone: false,
  templateUrl: './student-view.html',
  styleUrl: './student-view.scss'
})
export class StudentView implements OnChanges {

  @Input() student: Student | null = null;
  @Output() back = new EventEmitter<void>();

  activeTab: 'parents' | 'history' = 'parents';
  statusHistory: StatusChange[] = [];
  loadingHistory = false;
  errorMessage = '';

  constructor(private backend: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['student'] && this.student) {
      this.activeTab = 'parents';
      this.statusHistory = [];
    }
  }

  onTabChange(tab: 'parents' | 'history'): void {
    this.activeTab = tab;

    if (tab === 'history' && this.statusHistory.length === 0 && this.student) {
      this.loadStatusHistory();
    }
  }

  loadStatusHistory(): void {
    if (!this.student) return;

    this.loadingHistory = true;
    this.errorMessage = '';

    this.backend.get<StatusChange[]>(`/students/${this.student.id}/status-history`).subscribe({
      next: (history) => {
        this.statusHistory = history;
        this.loadingHistory = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load status history.';
        this.loadingHistory = false;
      }
    });
  }

  onBack(): void {
    this.back.emit();
  }
}
