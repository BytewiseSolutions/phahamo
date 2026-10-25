import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { StudentParent } from '../student-parent';
import { ParentPayload } from '../../parent/parent';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';

@Component({
  selector: 'app-student-parent-list',
  standalone: false,
  templateUrl: './student-parent-list.html',
  styleUrl: './student-parent-list.scss'
})
export class StudentParentList implements OnChanges {

  @Input() studentId!: number;

  parents: StudentParent[] = [];
  loading = false;
  errorMessage = '';

  showCreateForm = false;
  showLinkForm = false;

  private loaded = false;

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['studentId'] && this.studentId && !this.loaded) {
      this.loadParents();
    }
  }

  loadParents(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<StudentParent[]>(`/students/${this.studentId}/parents`).subscribe({
      next: (parents) => {
        this.parents = parents;
        this.loading = false;
        this.loaded = true;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load parents.';
        this.loading = false;
      }
    });
  }

  onAddParent(): void {
    this.showCreateForm = true;
    this.errorMessage = '';
  }

  onFormClosed(): void {
    this.showCreateForm = false;
  }

  onParentCreated(payload: ParentPayload): void {
    this.errorMessage = '';

    this.backend.postSilently<StudentParent>(`/students/${this.studentId}/parents`, payload).subscribe({
      next: () => {
        this.showCreateForm = false;
        this.loadParents();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to add parent.';
      }
    });
  }

  onLinkExisting(): void {
    this.showLinkForm = true;
    this.errorMessage = '';
  }
  onLinkFormClosed(): void {
    this.showLinkForm = false;
  }
  onParentLinked(parentId: number): void {
    this.backend.post<StudentParent>(`/students/${this.studentId}/parents/${parentId}`, {}).subscribe({
      next: () => {
        this.showLinkForm = false;
        this.loadParents();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to link parent.';
      }
    });
  }

  async onInvite(parent: StudentParent): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: parent.loginEnabled ? 'Re-invite Parent' : 'Invite Parent',
      message: `Send login credentials to ${parent.email}?`,
      confirmText: parent.loginEnabled ? 'Re-invite' : 'Invite',
      cancelText: 'Cancel',
      danger: false
    });
    if (!confirmed) return;

    this.backend.postSilently<StudentParent>(`/parents/${parent.id}/invite`, {}).subscribe({
      next: () => this.loadParents(),
      error: (err) => this.errorMessage = err.error?.message || 'Failed to invite parent.'
    });
  }

  async onDisableLogin(parent: StudentParent): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Disable Parent Login',
      message: `Disable portal access for ${parent.firstName} ${parent.lastName}? Email statements will continue to work.`,
      confirmText: 'Disable login',
      cancelText: 'Cancel',
      danger: true
    });
    if (!confirmed) return;

    this.backend.postSilently<void>(`/parents/${parent.id}/disable-login`, {}).subscribe({
      next: () => this.loadParents(),
      error: (err) => this.errorMessage = err.error?.message || 'Failed to disable parent login.'
    });
  }
}
