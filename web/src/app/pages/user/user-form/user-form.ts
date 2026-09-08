import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NewUser } from '../../../util/user';

@Component({
  selector: 'app-user-form',
  standalone: false,
  templateUrl: './user-form.html',
  styleUrl: './user-form.scss'
})
export class UserForm {
  @Input() visible = false;
  @Output() closed = new EventEmitter<void>();
  @Output() userCreated = new EventEmitter<NewUser>();

  userForm: FormGroup;
  availableRoles = ['PRINCIPAL', 'TEACHER', 'FINANCE_OFFICER', 'PARENT', 'STUDENT'];

  constructor(private fb: FormBuilder) {
    this.userForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      role: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    const formValue = this.userForm.value;

    const newUser: NewUser = {
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      email: formValue.email,
      phone: formValue.phone,
      roles: [formValue.role]
    };

    this.userCreated.emit(newUser);
    this.userForm.reset();
  }

  onClose(): void {
    this.userForm.reset();
    this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }
}
