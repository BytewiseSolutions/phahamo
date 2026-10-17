import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import { Announcement } from '../announcement';

@Component({
  selector: 'app-announcement-list',
  standalone: false,
  templateUrl: './announcement-list.html',
  styleUrl: './announcement-list.scss'
})
export class AnnouncementList implements OnInit {
  announcements: Announcement[] = [];
  loading = false;
  errorMessage = '';

  showForm = false;
  editingAnnouncement: Announcement | null = null;

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.backend.get<Announcement[]>('/announcements').subscribe({
      next: (rows) => {
        this.announcements = rows;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load announcements.';
        this.loading = false;
      }
    });
  }

  openCreate(): void {
    this.editingAnnouncement = null;
    this.showForm = true;
  }

  openEdit(a: Announcement): void {
    this.editingAnnouncement = a;
    this.showForm = true;
  }

  onSaved(): void {
    this.showForm = false;
    this.load();
  }

  onClosed(): void {
    this.showForm = false;
  }

  async delete(a: Announcement): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Delete Announcement',
      message: `Are you sure you want to delete "${a.title}"?`,
      confirmText: 'Delete',
      danger: true
    });
    if (!confirmed) return;

    this.backend.delete<void>(`/announcements/${a.id}`).subscribe({
      next: () => this.load(),
      error: () => this.errorMessage = 'Failed to delete.'
    });
  }
}
