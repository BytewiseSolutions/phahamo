import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BackendService } from '../../../util/backend.service';

interface UserDetail {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  status: string;
  roles: string[];
  createdAt?: string;
  updatedAt?: string;
  student?: {
    id: number;
    admissionNumber: string;
    dateOfBirth: string;
    gender: string;
    status: string;
    currentClassName?: string;
    schoolClassId?: number;
  } | null;
  parent?: {
    id: number;
    relationship: string;
  } | null;
  teacher?: {
    id: number;
    employeeNumber: string;
    qualifications: string;
    specialization: string;
    dateJoined: string;
    employmentType: string;
  } | null;
}

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

  onEdit(): void {
    if (this.user) {
      this.router.navigate(['/users'], { queryParams: { edit: this.user.id } });
    }
  }

  formatDate(value?: string): string {
    if (!value) return '—';
    try {
      return new Date(value).toLocaleDateString(undefined, {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    } catch {
      return value;
    }
  }

  formatInstant(value?: string): string {
    if (!value) return '—';
    try {
      return new Date(value).toLocaleString(undefined, {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return value;
    }
  }

  get roleLabel(): string {
    if (!this.user || this.user.roles.length === 0) return '—';
    return this.user.roles
      .map(r => r.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase()))
      .join(', ');
  }
}
