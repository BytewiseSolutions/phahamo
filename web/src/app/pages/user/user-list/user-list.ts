import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { User } from '../../../util/user';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import {Router} from '@angular/router';
import {AuthService} from '../../../util/auth.service';

@Component({
  selector: 'app-user-list',
  standalone: false,
  templateUrl: './user-list.html',
  styleUrl: './user-list.scss'
})
export class UserList implements OnInit {
  users: User[] = [];
  loading = false;
  showCreateForm = false;
  editingUser: User | null = null;
  errorMessage = '';
  activeTab = 'ALL';
  searchTerm = '';
  statusFilter = 'ALL';

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService,
    private router: Router,
    private authService: AuthService
  ) {}

  get canManageUsers(): boolean {
    return this.authService.hasRole('SCHOOL_ADMIN') || this.authService.hasRole('SYSTEM_ADMIN');
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
    let list = this.users;

    if (this.activeTab !== 'ALL') {
      list = list.filter(u => u.roles.includes(this.activeTab));
    }

    if (this.statusFilter !== 'ALL') {
      list = list.filter(u => u.status === this.statusFilter);
    }

    const q = this.searchTerm.trim().toLowerCase();
    if (q) {
      list = list.filter(u =>
        u.firstName.toLowerCase().includes(q) ||
        u.lastName.toLowerCase().includes(q) ||
        u.email.toLowerCase().includes(q) ||
        u.phone.toLowerCase().includes(q)
      );
    }

    return list;
  }

  countByRole(role: string): number {
    if (role === 'ALL') return this.users.length;
    return this.users.filter(u => u.roles.includes(role)).length;
  }

  setTab(role: string): void {
    this.activeTab = role;
    this.errorMessage = '';
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<User[]>('/users').subscribe({
      next: (users) => {
        this.users = users;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load users.';
        this.loading = false;
      }
    });
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
  }

  onCreateUser(newUser: any): void {
    this.errorMessage = '';
    this.showCreateForm = false;

    this.backend.postSilently<User>('/users', newUser).subscribe({
      next: (created) => {
        this.users = [created, ...this.users];

        const createdRole = created.roles?.[0];
        if (createdRole && this.tabs.some(t => t.role === createdRole)) {
          this.activeTab = createdRole;
        }
      },
      error: (err) => {
        this.errorMessage = err.status === 409
          ? (err.error?.message || 'A user with this email already exists.')
          : 'Something went wrong. Please try again.';
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

  onView(user: User): void {
    this.router.navigate(['/users', user.id]);
  }

  goToImport(): void {
    this.router.navigate(['/import']);
  }
}
