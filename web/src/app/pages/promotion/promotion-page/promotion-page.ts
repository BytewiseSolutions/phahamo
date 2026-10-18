import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { SchoolClass } from '../../class/class';
import { Student } from '../../student/student';
import { PromotionAction } from '../promotion-action';
import { PromotionHistoryRow } from '../promotion-history-row';
import { PromotionTab } from '../promotion-tab';
import {PromotionPayload, PromotionResponse} from '../promotion';

@Component({
  selector: 'app-promotion-page',
  standalone: false,
  templateUrl: './promotion-page.html',
  styleUrl: './promotion-page.scss'
})
export class PromotionPage implements OnInit {

  readonly PromotionAction = PromotionAction;

  activeTab: PromotionTab = 'promote';

  classes: SchoolClass[] = [];

  fromClassId: number | null = null;
  toClassId: number | null = null;
  action: PromotionAction = PromotionAction.PROMOTE;
  reason = 'End-of-year promotion';

  preview: Student[] = [];
  selectedIds = new Set<number>();

  history: PromotionHistoryRow[] = [];

  loading = false;
  saving = false;
  historyLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.backend.get<SchoolClass[]>('/classes').subscribe({
      next: (res) => {
        this.classes = res;
      },
      error: () => this.errorMessage = 'Failed to load classes.'
    });

    this.loadHistory();
  }

  setTab(tab: PromotionTab): void {
    this.activeTab = tab;
    this.successMessage = '';
    this.errorMessage = '';
    if (tab === 'history') {
      this.loadHistory();
    }
  }

  loadHistory(): void {
    this.historyLoading = true;

    this.backend.get<PromotionHistoryRow[]>('/promotions/history').subscribe({
      next: (res) => {
        this.history = res;
        this.historyLoading = false;
      },
      error: () => {
        this.historyLoading = false;
      }
    });
  }

  get allSelected(): boolean {
    return this.preview.length > 0 && this.selectedIds.size === this.preview.length;
  }

  get canConfirm(): boolean {
    if (!this.fromClassId) return false;
    if (this.action === PromotionAction.PROMOTE && !this.toClassId) return false;
    if (this.selectedIds.size === 0) return false;
    if (!this.reason || !this.reason.trim()) return false;
    return true;
  }

  get targetClassName(): string {
    if (!this.toClassId) return '';
    const c = this.classes.find(x => x.id === this.toClassId);
    return c ? c.name : '';
  }

  get selectedCount(): number {
    return this.selectedIds.size;
  }

  onFromClassChange(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.preview = [];
    this.selectedIds.clear();

    if (!this.fromClassId) return;

    this.loading = true;

    this.backend.get<Student[]>(
      `/promotions/preview?fromClassId=${this.fromClassId}`
    ).subscribe({
      next: (res) => {
        this.preview = res;
        this.selectedIds = new Set(res.map(s => s.id));
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load preview.';
        this.loading = false;
      }
    });
  }

  onActionChange(): void {
    this.errorMessage = '';
    this.successMessage = '';
    if (this.action === PromotionAction.LEAVE) {
      this.toClassId = null;
    }
  }

  onToggleStudent(studentId: number): void {
    if (this.selectedIds.has(studentId)) {
      this.selectedIds.delete(studentId);
    } else {
      this.selectedIds.add(studentId);
    }
  }

  onToggleAll(event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
    if (checked) {
      this.selectedIds = new Set(this.preview.map(s => s.id));
    } else {
      this.selectedIds.clear();
    }
  }

  reset(): void {
    this.fromClassId = null;
    this.toClassId = null;
    this.action = PromotionAction.PROMOTE;
    this.reason = 'End-of-year promotion';
    this.preview = [];
    this.selectedIds.clear();
    this.errorMessage = '';
  }

  onConfirm(): void {
    if (!this.canConfirm || !this.fromClassId) return;

    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const count = this.selectedIds.size;

    const payload: PromotionPayload = {
      fromClassId: this.fromClassId,
      action: this.action,
      studentIds: Array.from(this.selectedIds),
      reason: this.reason.trim()
    };

    if (this.action === PromotionAction.PROMOTE && this.toClassId) {
      payload.toClassId = this.toClassId;
    }

    this.backend.postSilently<PromotionResponse>('/promotions', payload).subscribe({
      next: () => {
        this.saving = false;
        const verb = this.action === PromotionAction.PROMOTE ? 'promoted' : 'marked as leaving';
        this.successMessage = `${count} student${count > 1 ? 's' : ''} ${verb}.`;
        this.reset();
        this.loadHistory();
        this.activeTab = 'history';
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to complete promotion.';
        this.saving = false;
      }
    });
  }

  formatDate(value: string): string {
    if (!value) return '—';
    try {
      return new Date(value).toLocaleDateString('en-GB', {
        day: 'numeric', month: 'short', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
      });
    } catch {
      return value;
    }
  }
}
