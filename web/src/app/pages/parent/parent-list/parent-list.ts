import { Component, OnInit } from '@angular/core';
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
export class ParentList implements OnInit {

  parents: Parent[] = [];
  loading = false;
  showCreateForm = false;
  editingParent: Parent | null = null;
  errorMessage = '';

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService,
    private successDialog: SuccessDialogService
  ) {}

  ngOnInit(): void {
    this.loadParents();
  }

  loadParents(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<Parent[]>('/parents').subscribe({
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
    this.showCreateForm = !this.showCreateForm;
    this.errorMessage = '';
  }

  onEdit(parent: Parent): void {
    this.editingParent = parent;
    this.showCreateForm = true;
    this.errorMessage = '';
  }

  onFormClosed(): void {
    this.showCreateForm = false;
    this.editingParent = null;
  }

  onCreateParent(payload: ParentPayload): void {
    this.errorMessage = '';

    this.backend.post<Parent>('/parents', payload).subscribe({
      next: () => {
        this.showCreateForm = false;
        this.loadParents();
        this.successDialog.show({ message: 'Parent registered.' });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to register parent.';
      }
    });
  }

  onUpdateParent(event: { id: number; payload: ParentPayload }): void {
    this.errorMessage = '';

    this.backend.put<Parent>(
      `/parents/${event.id}`,
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

  async onDelete(parent: Parent): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Delete Parent',
      message: `Delete "${parent.firstName} ${parent.lastName}"?`,
      confirmText: 'Delete',
      danger: true
    });

    if (!confirmed) return;

    this.errorMessage = '';

    this.backend.delete<void>(`/parents/${parent.id}`).subscribe({
      next: () => {
        this.loadParents();
        this.successDialog.show({ message: 'Parent deleted.' });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to delete parent.';
      }
    });
  }
}
