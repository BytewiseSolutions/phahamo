import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { User } from '../../../util/user';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';

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
  successMessage = '';

  constructor(
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService
  ) {}

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
    this.successMessage = '';
  }

  onEdit(user: User): void {
    this.editingUser = user;
    this.showCreateForm = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onFormClosed(): void {
    this.showCreateForm = false;
    this.editingUser = null;
  }

  onCreateUser(newUser: any): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.backend.post<User>('/users', newUser).subscribe({
      next: () => {
        this.successMessage = 'User created. A welcome email with login details has been sent.';
        this.showCreateForm = false;
        this.loadUsers();
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
    this.successMessage = '';

    this.backend.put<User>(`/users/${event.id}`, event.payload).subscribe({
      next: () => {
        this.successMessage = 'User updated.';
        this.showCreateForm = false;
        this.editingUser = null;
        this.loadUsers();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update user.';
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
    this.successMessage = '';

    this.backend.delete<void>(`/users/${user.id}`).subscribe({
      next: () => this.loadUsers(),
      error: () => {
        this.errorMessage = 'Failed to deactivate user.';
      }
    });
  }

  onActivate(userId: number): void {
    this.errorMessage = '';
    this.successMessage = '';

    this.backend.patch<void>(`/users/${userId}/activate`, {}).subscribe({
      next: () => this.loadUsers(),
      error: () => {
        this.errorMessage = 'Failed to activate user.';
      }
    });
  }
}
