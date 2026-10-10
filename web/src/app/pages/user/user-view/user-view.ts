import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { UserDetail } from '../../../util/user-detail';
import {SubjectClassAssignment} from '../../subject-class-assignment/subject-class-assignment';

interface UserTab {
  key: string;
  label: string;
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

  activeTab = '';

  assignments: SubjectClassAssignment[] = [];
  loadingAssignments = false;
  assignmentsError = '';

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
        this.activeTab = this.tabs[0]?.key ?? '';
        if (this.activeTab === 'subjects') {
          this.loadAssignments();
        }
      },
      error: (err) => {
        this.errorMessage = err.status === 404
          ? 'User not found.'
          : 'Failed to load user.';
        this.loading = false;
      }
    });
  }

  get tabs(): UserTab[] {
    if (!this.user) return [];

    if (this.user.teacher) {
      return [
        { key: 'subjects',   label: 'Subjects' },
        { key: 'timetable',  label: 'Timetable' },
        { key: 'lessons',    label: 'Lessons' },
        { key: 'attendance', label: 'Attendance' }
      ];
    }

    if (this.user.student) {
      return [
        { key: 'parents',         label: 'Parents' },
        { key: 'status-history',  label: 'Status History' }
      ];
    }

    return [];
  }

  setTab(key: string): void {
    this.activeTab = key;

    if (key === 'subjects' && this.assignments.length === 0 && !this.loadingAssignments) {
      this.loadAssignments();
    }
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
    return this.user.roles.map(r => this.formatEnum(r)).join(', ');
  }

  formatEnum(value?: string): string {
    if (!value) return '—';
    return value.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }

  loadAssignments(): void {
    const userId = this.user?.teacher?.userId;
    if (!userId) {
      this.assignments = [];
      return;
    }

    this.loadingAssignments = true;
    this.assignmentsError = '';

    this.backend.get<SubjectClassAssignment[]>(
      `/subject-class-assignments/teacher/${userId}`
    ).subscribe({
      next: (assignments) => {
        this.assignments = assignments;
        this.loadingAssignments = false;
      },
      error: (err) => {
        this.assignmentsError =
          err.error?.message || 'Failed to load teacher subjects.';
        this.loadingAssignments = false;
      }
    });
  }
}
