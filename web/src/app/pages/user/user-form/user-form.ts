import { Component, EventEmitter, Input, OnChanges, OnDestroy, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { User } from '../../../util/user';

@Component({
  selector: 'app-user-form',
  standalone: false,
  templateUrl: './user-form.html',
  styleUrl: './user-form.scss'
})
export class UserForm implements OnChanges, OnDestroy {
  @Input() visible = false;
  @Input() editingUser: User | null = null;
  @Output() closed = new EventEmitter<void>();
  @Output() userCreated = new EventEmitter<any>();
  @Output() userUpdated = new EventEmitter<{ id: number; payload: any }>();

  userForm: FormGroup;
  selectedRole = '';
  availableRoles = ['PRINCIPAL', 'TEACHER', 'FINANCE_OFFICER', 'STUDENT'];
  availableStatuses = ['ACTIVE', 'INACTIVE', 'SUSPENDED'];
  availableGenders = ['MALE', 'FEMALE'];
  availableEmploymentTypes = ['FULL_TIME', 'PART_TIME', 'CONTRACT'];

  private destroy$ = new Subject<void>();

  constructor(private fb: FormBuilder) {
    this.userForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.pattern(/^\S+\s+\S+.*$/)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      role: ['', Validators.required],
      status: ['ACTIVE'],

      studentDateOfBirth: [''],
      studentGender: [''],

      teacherQualifications: [''],
      teacherSpecialization: [''],
      teacherDateJoined: [''],
      teacherEmploymentType: ['']
    });

    this.userForm.get('role')!.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((role) => this.onRoleChanged(role));
  }

  get isEditMode(): boolean {
    return this.editingUser !== null;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['editingUser']) {
      if (this.editingUser) {
        this.userForm.patchValue({
          fullName: `${this.editingUser.firstName} ${this.editingUser.lastName}`.trim(),
          email: this.editingUser.email,
          phone: this.editingUser.phone,
          role: this.editingUser.roles[0] ?? '',
          status: this.editingUser.status
        });
        this.userForm.get('email')?.disable();
        this.selectedRole = this.editingUser.roles[0] ?? '';
      } else {
        this.userForm.get('email')?.enable();
        this.userForm.reset({ status: 'ACTIVE' });
        this.selectedRole = '';
      }
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private onRoleChanged(role: string): void {
    this.selectedRole = role;
    if (this.isEditMode) return;

    const studentControls = ['studentDateOfBirth', 'studentGender'];
    const teacherControls = ['teacherQualifications', 'teacherEmploymentType'];

    [...studentControls, ...teacherControls].forEach(name => {
      this.userForm.get(name)?.clearValidators();
      this.userForm.get(name)?.updateValueAndValidity();
    });

    if (role === 'STUDENT') {
      this.userForm.get('studentDateOfBirth')?.setValidators(Validators.required);
      this.userForm.get('studentGender')?.setValidators(Validators.required);
    }

    if (role === 'TEACHER') {
      this.userForm.get('teacherQualifications')?.setValidators(Validators.required);
      this.userForm.get('teacherEmploymentType')?.setValidators(Validators.required);
    }

    [...studentControls, ...teacherControls].forEach(name =>
      this.userForm.get(name)?.updateValueAndValidity()
    );
  }

  private splitFullName(fullName: string): { firstName: string; lastName: string } {
    const trimmed = fullName.trim();
    const firstSpaceIndex = trimmed.indexOf(' ');
    if (firstSpaceIndex === -1) return { firstName: trimmed, lastName: '' };
    return {
      firstName: trimmed.substring(0, firstSpaceIndex),
      lastName: trimmed.substring(firstSpaceIndex + 1).trim()
    };
  }

  onSubmit(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    const formValue = this.userForm.getRawValue();
    const { firstName, lastName } = this.splitFullName(formValue.fullName);

    if (this.isEditMode && this.editingUser) {
      this.userUpdated.emit({
        id: this.editingUser.id,
        payload: { firstName, lastName, phone: formValue.phone, roles: [formValue.role], status: formValue.status }
      });
      this.userForm.reset({ status: 'ACTIVE' });
      this.selectedRole = '';
      return;
    }

    const payload: any = { firstName, lastName, email: formValue.email, phone: formValue.phone, roles: [formValue.role] };

    if (formValue.role === 'STUDENT') {
      payload.student = {
        dateOfBirth: formValue.studentDateOfBirth ? new Date(formValue.studentDateOfBirth).toISOString() : null,
        gender: formValue.studentGender
      };
    }

    if (formValue.role === 'TEACHER') {
      payload.teacher = {
        qualifications: formValue.teacherQualifications,
        specialization: formValue.teacherSpecialization || null,
        dateJoined: formValue.teacherDateJoined || null,
        employmentType: formValue.teacherEmploymentType
      };
    }

    this.userCreated.emit(payload);
    this.userForm.reset({ status: 'ACTIVE' });
    this.selectedRole = '';
  }

  onClose(): void {
    this.userForm.reset({ status: 'ACTIVE' });
    this.selectedRole = '';
    this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) this.onClose();
  }
}
