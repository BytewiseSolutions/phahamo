import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BackendService } from '../../../util/backend.service';
import { UserDetail } from '../../../util/user-detail';
import {SubjectClassAssignment} from '../../subject-class-assignment/subject-class-assignment';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';

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
export class UserView implements OnInit, OnDestroy {
  user: UserDetail | null = null;
  loading = false;
  errorMessage = '';

  activeTab = '';

  assignments: SubjectClassAssignment[] = [];
  loadingAssignments = false;
  assignmentsError = '';
  photoUploading = false;
  studentPhotoUrl: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private backend: BackendService,
    private confirmDialog: ConfirmDialogService
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
        this.loadStudentPhoto(user.student?.photoUrl);
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
        { key: 'lessons',    label: 'Lessons' }
      ];
    }

    if (this.user.student) {
      return [
        { key: 'parents',         label: 'Parents' }
      ];
    }

    return [];
  }

  setTab(key: string): void {
    this.activeTab = key;
  }

  goBack(): void {
    this.router.navigate(['/users']);
  }

  photoSrc(): string {
    return this.studentPhotoUrl ?? '';
  }

  private loadStudentPhoto(photoUrl?: string): void {
    if (this.studentPhotoUrl) {
      URL.revokeObjectURL(this.studentPhotoUrl);
      this.studentPhotoUrl = null;
    }
    if (!photoUrl) return;

    this.backend.getBlob(photoUrl).subscribe({
      next: (blob) => {
        this.studentPhotoUrl = URL.createObjectURL(blob);
      },
      error: () => {
        this.studentPhotoUrl = null;
      }
    });
  }

  studentInitials(): string {
    if (!this.user) return '';
    return `${this.user.firstName.charAt(0)}${this.user.lastName.charAt(0)}`.toUpperCase();
  }

  uploadStudentPhoto(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    const studentId = this.user?.student?.id;
    if (!file || !studentId) return;

    if (file.size > 2 * 1024 * 1024) {
      this.errorMessage = 'Student photos must be 2MB or smaller.';
      input.value = '';
      return;
    }

    const form = new FormData();
    form.append('file', file, file.name);
    this.photoUploading = true;
    this.errorMessage = '';
    this.backend.upload<any>(`/students/${studentId}/photo`, form).subscribe({
      next: student => {
        if (this.user?.student) {
          this.user.student.photoUrl = student.photoUrl;
          this.loadStudentPhoto(student.photoUrl);
        }
        this.photoUploading = false;
        input.value = '';
      },
      error: err => {
        this.errorMessage = err.error?.message || 'Photo could not be uploaded.';
        this.photoUploading = false;
        input.value = '';
      }
    });
  }

  async removeStudentPhoto(): Promise<void> {
    const studentId = this.user?.student?.id;
    if (!studentId || !this.user?.student?.photoUrl) return;
    const confirmed = await this.confirmDialog.confirm({
      title: 'Remove Student Photo',
      message: 'Remove this student photo?',
      confirmText: 'Remove',
      danger: true
    });
    if (!confirmed) return;
    this.backend.delete<void>(`/students/${studentId}/photo`).subscribe({
      next: () => {
        if (this.user?.student) this.user.student.photoUrl = undefined;
        this.loadStudentPhoto();
      },
      error: () => this.errorMessage = 'Photo could not be removed.'
    });
  }

  ngOnDestroy(): void {
    if (this.studentPhotoUrl) {
      URL.revokeObjectURL(this.studentPhotoUrl);
    }
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

  goToAssignSubjects(): void {
    this.router.navigate(['/subject-class-assignments'], {
      queryParams: { teacherId: this.user?.teacher?.userId }
    });
  }
}
