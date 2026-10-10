import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { UserDetail } from '../../../util/user-detail';

@Component({
  selector: 'app-user-view',
  standalone: false,
  templateUrl: './user-view.html',
  styleUrl: './user-view.scss'
})
export class UserView implements OnInit {
  user: UserDetail | null = null;
  loading = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private backend: BackendService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.router.navigate(['/users']);
      return;
    }
    this.loadUser(Number(id));
  }

  loadUser(id: number): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<UserDetail>(`/users/${id}`).subscribe({
      next: (user) => {
        this.user = user;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.status === 404
          ? 'User not found.'
          : 'Failed to load user.';
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/users']);
  }

  formatDate(value?: string): string {
    if (!value) return '—';
    try {
      return new Date(value).toLocaleDateString('en-GB', {
        day: 'numeric',
        month: 'long',
        year: 'numeric'
      });
    } catch {
      return value;
    }
  }

  get roleLabel(): string {
    if (!this.user || this.user.roles.length === 0) return '—';
    return this.user.roles
      .map(r => this.formatEnum(r))
      .join(', ');
  }

  formatEnum(value?: string): string {
    if (!value) return '—';
    return value.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }
}
