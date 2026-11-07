import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../util/auth.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login implements OnInit {
  loginForm: FormGroup;
  submitted = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
      rememberMe: [false]
    });
  }

  ngOnInit(): void {
    const err = this.route.snapshot.queryParamMap.get('error');
    if (err === 'no_account') {
      this.errorMessage = 'No account exists for that Google email. Please contact your administrator.';
    } else if (err === 'inactive') {
      this.errorMessage = 'Your account is not active.';
    } else if (err === 'oauth_failed') {
      this.errorMessage = 'Google sign-in failed. Please try again.';
    }
  }

  onGoogleLogin(): void {
    this.authService.loginWithGoogle();
  }

  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const { email, password } = this.loginForm.value;

    this.authService.login({ email, password }).subscribe({
      next: (response) => this.router.navigate([
        response.requiresLegalAcceptance ? '/legal-acceptance' : '/dashboard'
      ]),
      error: (err) => {
        this.errorMessage = err.status === 401
          ? (err.error?.message || 'Invalid email or password.')
          : 'Something went wrong. Please try again.';
      }
    });
  }
}
