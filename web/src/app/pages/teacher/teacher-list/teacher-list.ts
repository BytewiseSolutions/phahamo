import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { Teacher, TeacherPayload } from '../teacher';
import { EmploymentType } from '../employment-type';

@Component({
  selector: 'app-teacher-list',
  standalone: false,
  templateUrl: './teacher-list.html',
  styleUrl: './teacher-list.scss'
})
export class TeacherList implements OnInit {

  teachers: Teacher[] = [];
  filteredTeachers: Teacher[] = [];

  loading = false;
  errorMessage = '';

  showCreateForm = false;
  editingTeacher: Teacher | null = null;

  showTeacherView = false;
  viewingTeacher: Teacher | null = null;

  searchTerm = '';
  employmentTypeFilter: EmploymentType | '' = '';

  employmentTypes = Object.values(EmploymentType);

  constructor(
    private backend: BackendService
  ) {}

  ngOnInit(): void {
    this.loadTeachers();
  }

  loadTeachers(): void {
    this.loading = true;
    this.errorMessage = '';

    this.backend.get<Teacher[]>('/teachers').subscribe({
      next: (teachers) => {
        this.teachers = teachers;
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || 'Failed to load teachers.';
        this.loading = false;
      }
    });
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onEmploymentTypeChange(): void {
    this.applyFilters();
  }

  private applyFilters(): void {
    const search = this.searchTerm.trim().toLowerCase();

    this.filteredTeachers = this.teachers.filter(teacher => {

      const matchesSearch =
        !search ||
        teacher.employeeNumber?.toLowerCase().includes(search) ||
        teacher.user?.firstName?.toLowerCase().includes(search) ||
        teacher.user?.lastName?.toLowerCase().includes(search) ||
        teacher.user?.email?.toLowerCase().includes(search) ||
        teacher.qualifications?.toLowerCase().includes(search) ||
        teacher.specialization?.toLowerCase().includes(search);

      const matchesEmploymentType =
        !this.employmentTypeFilter ||
        teacher.employmentType === this.employmentTypeFilter;

      return matchesSearch && matchesEmploymentType;
    });
  }

  toggleCreateForm(): void {
    this.editingTeacher = null;
    this.showCreateForm = !this.showCreateForm;
    this.errorMessage = '';
  }

  onEdit(teacher: Teacher): void {
    this.editingTeacher = teacher;
    this.showCreateForm = true;
    this.errorMessage = '';
  }

  onView(teacher: Teacher): void {
    this.viewingTeacher = teacher;
    this.showTeacherView = true;
    this.errorMessage = '';
  }

  onTeacherViewBack(): void {
    this.showTeacherView = false;
    this.viewingTeacher = null;
  }

  onFormClosed(): void {
    this.showCreateForm = false;
    this.editingTeacher = null;
  }

  onTeacherCreated(payload: TeacherPayload): void {
    this.errorMessage = '';

    this.backend.postSilently<Teacher>(
      '/teachers',
      payload
    ).subscribe({
      next: () => {
        this.showCreateForm = false;
        this.editingTeacher = null;
        this.loadTeachers();
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || 'Failed to register teacher.';
      }
    });
  }

  onTeacherUpdated(event: {
    id: number;
    payload: TeacherPayload;
  }): void {
    this.errorMessage = '';

    this.backend.putSilently<Teacher>(
      `/teachers/${event.id}`,
      event.payload
    ).subscribe({
      next: () => {
        this.showCreateForm = false;
        this.editingTeacher = null;
        this.loadTeachers();
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || 'Failed to update teacher.';
      }
    });
  }

  get totalTeachers(): number {
    return this.teachers.length;
  }

  get fullTimeTeachers(): number {
    return this.teachers.filter(
      teacher => teacher.employmentType === EmploymentType.FULL_TIME
    ).length;
  }

  get partTimeTeachers(): number {
    return this.teachers.filter(
      teacher => teacher.employmentType === EmploymentType.PART_TIME
    ).length;
  }

  get contractTeachers(): number {
    return this.teachers.filter(
      teacher => teacher.employmentType === EmploymentType.CONTRACT
    ).length;
  }

  getEmploymentTypeLabel(type: EmploymentType): string {
    switch (type) {
      case EmploymentType.FULL_TIME:
        return 'Full Time';

      case EmploymentType.PART_TIME:
        return 'Part Time';

      case EmploymentType.CONTRACT:
        return 'Contract';

      default:
        return type;
    }
  }
}
