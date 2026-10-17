import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Announcement, AudienceType, AnnouncementStatus } from '../announcement';
import { AnnouncementPayload } from '../announcement-payload';
import { SchoolClass } from '../../class/class';

@Component({
  selector: 'app-announcement-form',
  standalone: false,
  templateUrl: './announcement-form.html',
  styleUrl: './announcement-form.scss'
})
export class AnnouncementForm implements OnChanges {
  @Input() visible = false;
  @Input() editingAnnouncement: Announcement | null = null;
  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  title = '';
  body = '';
  status: AnnouncementStatus = 'PUBLISHED';
  audienceType: AudienceType = 'ALL';

  targetClassId: number | null = null;
  targetRole = 'STUDENT';
  targetUserId: number | null = null;

  publishAt = '';
  expiresAt = '';

  classes: SchoolClass[] = [];
  users: any[] = [];
  saving = false;
  errorMessage = '';

  roles = ['STUDENT', 'PARENT', 'TEACHER'];

  // Search Dropdown State
  userSearchQuery = '';
  showUserDropdown = false;
  selectedUserLabel = '';

  constructor(private backend: BackendService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible']?.currentValue === true) {
      this.initForm();
      this.loadClasses();
      this.loadUsers();
    }
  }

  loadClasses(): void {
    this.backend.get<SchoolClass[]>('/classes').subscribe({
      next: (c) => this.classes = c
    });
  }

  loadUsers(): void {
    this.backend.get<any[]>('/users').subscribe({
      next: (u) => this.users = u
    });
  }

  fullName(u: any): string {
    return `${u.firstName} ${u.lastName}`;
  }

  get filteredUsers(): any[] {
    if (!this.userSearchQuery) return this.users;
    const q = this.userSearchQuery.toLowerCase();
    return this.users.filter(u =>
      this.fullName(u).toLowerCase().includes(q) ||
      (u.email && u.email.toLowerCase().includes(q)) ||
      (u.phone && u.phone.toLowerCase().includes(q))
    );
  }

  selectUser(user: any): void {
    this.targetUserId = user.id;
    this.selectedUserLabel = this.fullName(user);
    this.userSearchQuery = this.fullName(user);
    this.showUserDropdown = false;
  }

  clearUserSelection(): void {
    this.targetUserId = null;
    this.selectedUserLabel = '';
    this.userSearchQuery = '';
    this.showUserDropdown = false;
  }

  onUserSearchBlur(): void {
    setTimeout(() => this.showUserDropdown = false, 200);
  }

  initForm(): void {
    this.errorMessage = '';
    this.saving = false;

    if (this.editingAnnouncement) {
      this.title = this.editingAnnouncement.title;
      this.body = this.editingAnnouncement.body;
      this.status = this.editingAnnouncement.status;
      this.audienceType = this.editingAnnouncement.audienceType;
      this.targetClassId = this.editingAnnouncement.targetClassId ?? null;
      this.targetRole = this.editingAnnouncement.targetRole ?? 'STUDENT';

      this.targetUserId = this.editingAnnouncement.targetUserId ?? null;
      if (this.editingAnnouncement.targetUserId) {
        this.userSearchQuery = this.editingAnnouncement.targetUserName || 'Selected User';
        this.selectedUserLabel = this.editingAnnouncement.targetUserName || 'Selected User';
      } else {
        this.userSearchQuery = '';
        this.selectedUserLabel = '';
      }

      this.publishAt = this.editingAnnouncement.publishAt ? this.editingAnnouncement.publishAt.substring(0, 16) : '';
      this.expiresAt = this.editingAnnouncement.expiresAt ? this.editingAnnouncement.expiresAt.substring(0, 16) : '';
    } else {
      this.title = '';
      this.body = '';
      this.status = 'PUBLISHED';
      this.audienceType = 'ALL';
      this.targetClassId = null;
      this.targetRole = 'STUDENT';
      this.targetUserId = null;
      this.userSearchQuery = '';
      this.selectedUserLabel = '';
      this.publishAt = this.toDatetimeLocal(new Date());
      this.expiresAt = '';
    }
  }

  onSave(): void {
    if (!this.title || !this.body) {
      this.errorMessage = 'Title and body are required.';
      return;
    }

    const payload: AnnouncementPayload = {
      title: this.title,
      body: this.body,
      status: this.status,
      audienceType: this.audienceType,
      publishAt: this.publishAt ? new Date(this.publishAt).toISOString() : undefined,
      expiresAt: this.expiresAt ? new Date(this.expiresAt).toISOString() : undefined
    };

    if (this.audienceType === 'CLASS') {
      if (!this.targetClassId) { this.errorMessage = 'Please select a class.'; return; }
      payload.targetClassId = this.targetClassId;
    }
    if (this.audienceType === 'ROLE') {
      payload.targetRole = this.targetRole;
    }
    if (this.audienceType === 'USER') {
      if (!this.targetUserId) { this.errorMessage = 'Please select a user.'; return; }
      payload.targetUserId = this.targetUserId;
    }

    this.saving = true;
    const req = this.editingAnnouncement
      ? this.backend.putSilently<Announcement>(`/announcements/${this.editingAnnouncement.id}`, payload)
      : this.backend.postSilently<Announcement>('/announcements', payload);

    req.subscribe({
      next: () => { this.saving = false; this.saved.emit(); },
      error: (err) => { this.errorMessage = err.error?.message || 'Failed to save.'; this.saving = false; }
    });
  }

  onClose(): void {
    if (!this.saving) this.closed.emit();
  }

  private toDatetimeLocal(d: Date): string {
    const iso = d.toISOString();
    return iso.substring(0, 16);
  }
}
