import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { User } from '../../../util/user';

@Component({
  selector: 'app-user-form',
  standalone: false,
  templateUrl: './user-form.html',
  styleUrl: './user-form.scss'
})
export class UserForm implements OnChanges {
  @Input() visible = false;
  @Input() editingUser: User | null = null;
  @Output() closed = new EventEmitter<void>();
  @Output() userCreated = new EventEmitter<any>();
  @Output() userUpdated = new EventEmitter<{ id: number; payload: any }>();

  userForm: FormGroup;
  availableRoles = ['PRINCIPAL', 'TEACHER', 'FINANCE_OFFICER', 'PARENT', 'STUDENT'];
  availableStatuses = ['ACTIVE', 'INACTIVE', 'SUSPENDED'];

  constructor(private fb: FormBuilder) {
    this.userForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      role: ['', Validators.required],
      status: ['ACTIVE']
    });
  }

  get isEditMode(): boolean {
    return this.editingUser !== null;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['editingUser']) {
      if (this.editingUser) {
        this.userForm.patchValue({
          firstName: this.editingUser.firstName,
          lastName: this.editingUser.lastName,
          email: this.editingUser.email,
          phone: this.editingUser.phone,
          role: this.editingUser.roles[0] ?? '',
          status: this.editingUser.status
        });
        this.userForm.get('email')?.disable();
      } else {
        this.userForm.get('email')?.enable();
        this.userForm.reset({ status: 'ACTIVE' });
      }
    }
  }

  onSubmit(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    const formValue = this.userForm.getRawValue();

    if (this.isEditMode && this.editingUser) {
      this.userUpdated.emit({
        id: this.editingUser.id,
        payload: {
          firstName: formValue.firstName,
          lastName: formValue.lastName,
          phone: formValue.phone,
          roles: [formValue.role],
          status: formValue.status
        }
      });
    } else {
      this.userCreated.emit({
        firstName: formValue.firstName,
        lastName: formValue.lastName,
        email: formValue.email,
        phone: formValue.phone,
        roles: [formValue.role]
      });
    }

    this.userForm.reset({ status: 'ACTIVE' });
  }

  onClose(): void {
    this.userForm.reset({ status: 'ACTIVE' });
    this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }
}
