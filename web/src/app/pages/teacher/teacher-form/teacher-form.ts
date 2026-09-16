import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';
import { Teacher, TeacherPayload } from '../teacher';
import { EmploymentType } from '../employment-type';
import { User } from '../../../util/user';
import { BackendService } from '../../../util/backend.service';

@Component({
  selector: 'app-teacher-form',
  standalone: false,
  templateUrl: './teacher-form.html',
  styleUrl: './teacher-form.scss'
})
export class TeacherForm implements OnChanges {

  @Input() visible = false;
  @Input() editingTeacher: Teacher | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() teacherCreated = new EventEmitter<TeacherPayload>();
  @Output() teacherUpdated = new EventEmitter<{
    id: number;
    payload: TeacherPayload;
  }>();

  teacherForm: FormGroup;

  users: User[] = [];
  employmentTypes = Object.values(EmploymentType);

  loadingUsers = false;
  userLoadError = '';

  registrationMode: 'EXISTING_USER' | 'NEW_USER' = 'EXISTING_USER';

  constructor(
    private fb: FormBuilder,
    private backend: BackendService
  ) {
    this.teacherForm = this.fb.group({
      userId: ['', Validators.required],
      firstName: [''],
      lastName: [''],
      email: [''],
      phone: [''],
      qualifications: [''],
      dateJoined: [''],
      specialization: [''],
      employmentType: [
        EmploymentType.FULL_TIME,
        Validators.required
      ]
    });
  }

  get isEditMode(): boolean {
    return this.editingTeacher !== null;
  }

  get isExistingUserMode(): boolean {
    return this.registrationMode === 'EXISTING_USER';
  }

  get isNewUserMode(): boolean {
    return this.registrationMode === 'NEW_USER';
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible && !this.isEditMode) {
      this.loadUsers();
    }

    if (changes['editingTeacher']) {
      if (this.editingTeacher) {
        this.registrationMode = 'EXISTING_USER';

        this.teacherForm.patchValue({
          userId: this.editingTeacher.user?.id,
          firstName: this.editingTeacher.user?.firstName || '',
          lastName: this.editingTeacher.user?.lastName || '',
          email: this.editingTeacher.user?.email || '',
          phone: this.editingTeacher.user?.phone || '',
          qualifications: this.editingTeacher.qualifications || '',
          dateJoined: this.editingTeacher.dateJoined?.substring(0, 10) || '',
          specialization: this.editingTeacher.specialization || '',
          employmentType: this.editingTeacher.employmentType
        });

        this.teacherForm.get('userId')?.disable();
      } else {
        this.resetForm();
      }
    }
  }

  private loadUsers(): void {
    this.loadingUsers = true;
    this.userLoadError = '';

    this.backend.get<User[]>('/users/teachers').subscribe({
      next: (users) => {
        this.users = users;
        this.loadingUsers = false;
      },
      error: (err) => {
        this.users = [];
        this.userLoadError =
          err.error?.message || 'Failed to load teacher users.';
        this.loadingUsers = false;
      }
    });
  }

  setRegistrationMode(
    mode: 'EXISTING_USER' | 'NEW_USER'
  ): void {
    if (this.isEditMode) {
      return;
    }

    this.registrationMode = mode;

    const userIdControl = this.teacherForm.get('userId');
    const firstNameControl = this.teacherForm.get('firstName');
    const lastNameControl = this.teacherForm.get('lastName');
    const emailControl = this.teacherForm.get('email');

    if (mode === 'EXISTING_USER') {
      userIdControl?.setValidators([
        Validators.required
      ]);

      firstNameControl?.clearValidators();
      lastNameControl?.clearValidators();
      emailControl?.clearValidators();
    } else {
      userIdControl?.clearValidators();

      firstNameControl?.setValidators([
        Validators.required
      ]);

      lastNameControl?.setValidators([
        Validators.required
      ]);

      emailControl?.setValidators([
        Validators.required,
        Validators.email
      ]);
    }

    userIdControl?.updateValueAndValidity();
    firstNameControl?.updateValueAndValidity();
    lastNameControl?.updateValueAndValidity();
    emailControl?.updateValueAndValidity();
  }

  onSubmit(): void {
    if (this.teacherForm.invalid) {
      this.teacherForm.markAllAsTouched();
      return;
    }

    const formValue = this.teacherForm.getRawValue();

    const payload: TeacherPayload = {
      qualifications: formValue.qualifications?.trim() || undefined,
      dateJoined: formValue.dateJoined || undefined,
      specialization: formValue.specialization?.trim() || undefined,
      employmentType: formValue.employmentType
    };

    if (this.isEditMode && this.editingTeacher) {
      this.teacherUpdated.emit({
        id: this.editingTeacher.id,
        payload
      });
    } else if (this.isExistingUserMode) {
      payload.user = {
        id: Number(formValue.userId)
      };

      this.teacherCreated.emit(payload);
    } else {
      payload.user = {
        firstName: formValue.firstName.trim(),
        lastName: formValue.lastName.trim(),
        email: formValue.email.trim(),
        phone: formValue.phone?.trim() || undefined
      };

      this.teacherCreated.emit(payload);
    }

    this.resetForm();
  }

  onClose(): void {
    this.resetForm();
    this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
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

  private resetForm(): void {
    this.registrationMode = 'EXISTING_USER';
    this.userLoadError = '';

    this.teacherForm.reset({
      userId: '',
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      qualifications: '',
      dateJoined: '',
      specialization: '',
      employmentType: EmploymentType.FULL_TIME
    });

    this.teacherForm.get('userId')?.enable();

    const userIdControl = this.teacherForm.get('userId');
    const firstNameControl = this.teacherForm.get('firstName');
    const lastNameControl = this.teacherForm.get('lastName');
    const emailControl = this.teacherForm.get('email');

    userIdControl?.setValidators([
      Validators.required
    ]);

    firstNameControl?.clearValidators();
    lastNameControl?.clearValidators();
    emailControl?.clearValidators();

    userIdControl?.updateValueAndValidity();
    firstNameControl?.updateValueAndValidity();
    lastNameControl?.updateValueAndValidity();
    emailControl?.updateValueAndValidity();
  }
}
