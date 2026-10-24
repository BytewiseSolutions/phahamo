import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../util/auth.service';

@Component({
  selector: 'app-subscription-expired',
  standalone: false,
  templateUrl: './subscription-expired.html',
  styleUrl: './subscription-expired.scss'
})
export class SubscriptionExpired {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
