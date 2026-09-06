import { Component } from '@angular/core';
import { AuthService } from '../../util/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard {
  constructor(private authService: AuthService) {}

  onLogout(): void {
    this.authService.logout();
  }
}
