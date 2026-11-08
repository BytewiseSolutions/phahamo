import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { BackendService } from '../../../util/backend.service';
import { User } from '../../../util/user';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import {Router} from '@angular/router';
import {AuthService} from '../../../util/auth.service';
import { StudentDuplicate } from '../../../util/student-duplicate';
import { Page } from '../../../util/page';

@Component({
  selector: 'app-user-list',
  standalone: false,
  templateUrl: './user-list.html',
  styleUrl: './user-list.scss'
})
export class UserList implements OnInit, OnDestroy {
  users: User[] = [];
  loading = false;
  showCreateForm = false;
  editingUser: User | null = null;
  errorMessage = '';
  activeTab = 'ALL';
  searchTerm = '';
  statusFilter = 'ALL';
  currentPage = 1;
  readonly pageSize = 20;
  totalElements = 0;
  totalPages = 0;
  duplicateMatches: StudentDuplicate[] = [];
  pendingDuplicatePayload: any | null = null;
  private photoUrls = new Map<number, string>();
  private searchSubject = new Subject<void>();
  private destroy$ = new Subject<void>();

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService,
    private router: Router,
    private authService: AuthService
  ) {}

  get canManageUsers(): boolean {
    return this.authService.hasRole('SCHOOL_ADMIN') || this.authService.hasRole('SYSTEM_ADMIN');
  }

  photoSrc(photoUrl: string | undefined, userId: number): string {
    return photoUrl ? this.photoUrls.get(userId) ?? '' : '';
  }

  private loadStudentPhotos(users: User[]): void {
    this.photoUrls.forEach(url => URL.revokeObjectURL(url));
    this.photoUrls.clear();

    users
      .filter(user => !!user.student?.photoUrl)
      .forEach(user => {
        this.backend.getBlob(user.student!.photoUrl!).subscribe({
          next: blob => this.photoUrls.set(user.id, URL.createObjectURL(blob))
        });
      });
  }

  initials(user: User): string {
    return `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase();
  }

  tabs = [
    { role: 'ALL', label: 'All' },
    { role: 'SCHOOL_ADMIN', label: 'Admins' },
    { role: 'PRINCIPAL', label: 'Principals' },
    { role: 'TEACHER', label: 'Teachers' },
    { role: 'STUDENT', label: 'Students' },
    { role: 'FINANCE_OFFICER', label: 'Finance' },
  ];

  get filteredUsers(): User[] {
    return this.users;
  }

  get paginatedUsers(): User[] {
    return this.users;
  }

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, index) => index + 1);
  }

  isLocked(user: User): boolean {
    return !!user.lockedUntil && new Date(user.lockedUntil).getTime() > Date.now();
  }

  setTab(role: string): void {
    this.activeTab = role;
    this.currentPage = 1;
    this.errorMessage = '';
    this.loadUsers();
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.loadUsers();
    }
  }

  resetPagination(): void {
    this.searchSubject.next();
  }

  ngOnInit(): void {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.currentPage = 1;
      this.loadUsers();
    });
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.errorMessage = '';

    const params = new URLSearchParams();
    params.set('page', String(this.currentPage - 1));
    params.set('size', String(this.pageSize));
    if (this.searchTerm.trim()) params.set('search', this.searchTerm.trim());
    if (this.activeTab !== 'ALL') params.set('role', this.activeTab);
    if (this.statusFilter !== 'ALL') params.set('status', this.statusFilter);

    this.backend.get<Page<User>>(`/users/page?${params.toString()}`).subscribe({
      next: (result) => {
        const metadata = result.page ?? result;
        this.users = result.content;
        this.totalElements = metadata.totalElements ?? 0;
        this.totalPages = metadata.totalPages ?? 0;
        this.currentPage = (metadata.number ?? 0) + 1;
        this.loadStudentPhotos(this.users);
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load users.';
        this.loading = false;
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.photoUrls.forEach(url => URL.revokeObjectURL(url));
    this.photoUrls.clear();
  }

  toggleCreateForm(): void {
    this.editingUser = null;
    this.showCreateForm = !this.showCreateForm;
    this.errorMessage = '';
  }

  onEdit(user: User): void {
    this.editingUser = user;
    this.showCreateForm = true;
    this.errorMessage = '';
  }

  onFormClosed(): void {
    this.showCreateForm = false;
    this.editingUser = null;
  }
  get hasActiveFilters(): boolean {
    return this.searchTerm.trim().length > 0 || this.statusFilter !== 'ALL';
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.statusFilter = 'ALL';
    this.currentPage = 1;
  }

  onCreateUser(newUser: any): void {
    this.errorMessage = '';
    this.backend.postSilently<User>('/users', newUser).subscribe({
      next: (created) => {
        this.showCreateForm = false;
        this.duplicateMatches = [];
        this.pendingDuplicatePayload = null;
        this.loadUsers();

        const createdRole = created.roles?.[0];
        if (createdRole && this.tabs.some(t => t.role === createdRole)) {
          this.activeTab = createdRole;
        }
      },
      error: (err) => {
        if (err.status === 409 && err.error?.matches?.length) {
          this.duplicateMatches = err.error.matches;
          this.pendingDuplicatePayload = newUser;
          this.showCreateForm = false;
          return;
        }
        this.errorMessage = err.error?.message || 'Something went wrong. Please try again.';
      }
    });
  }

  cancelDuplicateRegistration(): void {
    this.duplicateMatches = [];
    this.pendingDuplicatePayload = null;
  }

  confirmDuplicateRegistration(): void {
    if (!this.pendingDuplicatePayload) return;
    const payload = {
      ...this.pendingDuplicatePayload,
      student: {
        ...this.pendingDuplicatePayload.student,
        duplicateConfirmed: true
      }
    };
    this.cancelDuplicateRegistration();
    this.onCreateUserConfirmed(payload);
  }

  openDuplicateStudent(student: StudentDuplicate): void {
    this.router.navigate(['/users', student.userId]);
  }

  private onCreateUserConfirmed(payload: any): void {
    this.backend.postSilently<User>('/users', payload).subscribe({
      next: (created) => {
        this.showCreateForm = false;
        this.loadUsers();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to create user.';
      }
    });
  }

  onUpdateUser(event: { id: number; payload: any }): void {
    this.errorMessage = '';
    this.showCreateForm = false;
    this.editingUser = null;

    this.backend.putSilently<User>(`/users/${event.id}`, event.payload).subscribe({
      next: (updated) => {
        this.users = this.users.map(u => u.id === event.id ? updated : u);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || err.error?.errors?.[0]?.defaultMessage || 'Failed to update user.';
      }
    });
  }

  async onDeactivate(user: User): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Deactivate User',
      message: `Deactivate "${user.firstName} ${user.lastName}"? They will no longer be able to log in.`,
      confirmText: 'Deactivate',
      danger: true
    });

    if (!confirmed) return;

    this.errorMessage = '';

    this.backend.delete<void>(`/users/${user.id}`).subscribe({
      next: () => this.loadUsers(),
      error: () => {
        this.errorMessage = 'Failed to deactivate user.';
      }
    });
  }

  onActivate(userId: number): void {
    this.errorMessage = '';

    this.backend.patch<void>(`/users/${userId}/activate`, {}).subscribe({
      next: () => this.loadUsers(),
      error: () => {
        this.errorMessage = 'Failed to activate user.';
      }
    });
  }

  async onUnlock(user: User): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Unlock User Account',
      message: `Unlock "${user.firstName} ${user.lastName}" so they can try logging in again?`,
      confirmText: 'Unlock'
    });

    if (!confirmed) return;

    this.errorMessage = '';
    this.backend.patch<void>(`/users/${user.id}/unlock`, {}).subscribe({
      next: () => this.loadUsers(),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to unlock user account.';
      }
    });
  }

  onView(user: User): void {
    this.router.navigate(['/users', user.id]);
  }

  goToImport(): void {
    this.router.navigate(['/import']);
  }
}
