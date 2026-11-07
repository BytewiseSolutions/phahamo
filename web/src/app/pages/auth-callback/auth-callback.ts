import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../../util/auth.service';

@Component({
  selector: 'app-auth-callback',
  standalone: false,
  templateUrl: './auth-callback.html',
  styleUrl: './auth-callback.scss'
})
export class AuthCallback implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (!token) {
      this.router.navigate(['/login'], { queryParams: { error: 'oauth_failed' } });
      return;
    }

    this.auth.acceptToken(token);
    this.router.navigate([this.route.snapshot.queryParamMap.get('legal') === 'true'
      ? '/legal-acceptance' : '/dashboard']);
  }
}
