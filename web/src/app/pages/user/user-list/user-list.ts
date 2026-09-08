import { Component, OnInit } from '@angular/core';
import {BackendService} from '../../../util/backend.service';
import {NewUser, User} from '../../../util/user';

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
  errorMessage = '';
  successMessage = '';

  constructor(private backend: BackendService) {}

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
    this.showCreateForm = !this.showCreateForm;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onCreateUser(newUser: NewUser): void {
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

  onDeactivate(userId: number): void {
    if (!confirm('Deactivate this user? They will no longer be able to log in.')) {
      return;
    }

    this.backend.delete<void>(`/users/${userId}`).subscribe({
      next: () => this.loadUsers(),
      error: () => {
        this.errorMessage = 'Failed to deactivate user.';
      }
    });
  }
}
