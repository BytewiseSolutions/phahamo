import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import { SuccessDialogService } from '../../../util/success-dialog.service';
import { Parent, ParentPayload } from '../parent';

@Component({
  selector: 'app-parent-list',
  standalone: false,
  templateUrl: './parent-list.html',
  styleUrl: './parent-list.scss'
})
export class ParentList implements OnChanges {

  @Input() studentId: number | null = null;

  parents: Parent[] = [];
  loading = false;
  showCreateForm = false;
  showLinkForm = false;
  editingParent: Parent | null = null;
  linkParentId = '';
  errorMessage = '';

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService,
    private successDialog: SuccessDialogService
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['studentId'] && this.studentId) {
      this.showCreateForm = false;
      this.showLinkForm = false;
      this.editingParent = null;
      this.loadParents();
    }
  }

  loadParents(): void {
    if (!this.studentId) return;

    this.loading = true;
    this.errorMessage = '';

    this.backend.get<Parent[]>(`/students/${this.studentId}/parents`).subscribe({
      next: (parents) => {
        this.parents = parents;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load parents.';
        this.loading = false;
      }
    });
  }

  toggleCreateForm(): void {
    this.editingParent = null;
    this.showLinkForm = false;
    this.showCreateForm = !this.showCreateForm;
    this.errorMessage = '';
  }

  toggleLinkForm(): void {
    this.editingParent = null;
    this.showCreateForm = false;
    this.showLinkForm = true;
    this.errorMessage = '';
  }

  onLinkFormClosed(): void {
    this.showLinkForm = false;
  }

  onEdit(parent: Parent): void {
    this.editingParent = parent;
    this.showLinkForm = false;
    this.showCreateForm = true;
    this.errorMessage = '';
  }

  onFormClosed(): void {
    this.showCreateForm = false;
    this.editingParent = null;
  }

  onCreateParent(payload: ParentPayload): void {
    if (!this.studentId) return;
    this.errorMessage = '';

    this.backend.post<Parent>(`/students/${this.studentId}/parents`, payload).subscribe({
      next: () => {
        this.showCreateForm = false;
        this.loadParents();
        this.successDialog.show({ message: 'Parent added.' });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to add parent.';
      }
    });
  }

  onUpdateParent(event: { id: number; payload: ParentPayload }): void {
    if (!this.studentId) return;
    this.errorMessage = '';

    this.backend.put<Parent>(
      `/students/${this.studentId}/parents/${event.id}`,
      event.payload
    ).subscribe({
      next: () => {
        this.showCreateForm = false;
        this.editingParent = null;
        this.loadParents();
        this.successDialog.show({ message: 'Parent updated.' });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update parent.';
      }
    });
  }

  onLinkExisting(parentId: number): void {
    if (!this.studentId) return;
    this.errorMessage = '';

    this.backend.post<Parent>(`/students/${this.studentId}/parents/${parentId}`, {}).subscribe({
      next: () => {
        this.showLinkForm = false;
        this.loadParents();
        this.successDialog.show({ message: 'Parent linked.' });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to link parent.';
      }
    });
  }

  async onUnlink(parent: Parent): Promise<void> {
    if (!this.studentId) return;

    const confirmed = await this.confirmDialog.confirm({
      title: 'Unlink Parent',
      message: `Unlink "${parent.firstName} ${parent.lastName}" from this student?`,
      confirmText: 'Unlink',
      danger: true
    });

    if (!confirmed) return;

    this.errorMessage = '';

    this.backend.delete<void>(`/students/${this.studentId}/parents/${parent.id}`).subscribe({
      next: () => {
        this.loadParents();
        this.successDialog.show({ message: 'Parent unlinked.' });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to unlink parent.';
      }
    });
  }
}
