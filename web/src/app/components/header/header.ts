import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from '../../util/auth.service';

@Component({
  selector: 'app-header',
  standalone: false,
  templateUrl: './header.html',
  styleUrl: './header.scss'
})
export class Header implements OnInit {
  fullName = '';
  role = '';
  initials = '';
  schoolName = '';
  pageTitle = '';

  private pageTitles: Record<string, string> = {
    '/dashboard': 'Dashboard',
    '/users': 'Users',
    '/teachers': 'Teachers',
    '/students': 'Students',
    '/classes': 'Classes',
    '/subjects': 'Subjects',
    '/timetable': 'Timetable',
    '/academic-years': 'Academic Years',
    '/reports': 'Reports',
    '/attendance': 'Attendance',
    '/grades': 'Grades',
    '/my-classes': 'My Classes',
    '/fees': 'Fees',
    '/payments': 'Payments',
    '/my-children': 'My Children',
    '/results': 'Results',
    '/schools': 'Schools',
    '/subscriptions': 'Subscriptions',
    '/profile': 'Profile',
    '/school-profile': 'School Profile',
  };

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.fullName = this.authService.getFullName();
    this.schoolName = this.authService.getSchoolName();
    const roles = this.authService.getRoles();
    this.role = roles.length ? this.formatRole(roles[0]) : '';
    this.initials = this.fullName
      .split(' ')
      .filter(n => n)
      .map(n => n[0])
      .join('')
      .substring(0, 2)
      .toUpperCase();

    this.pageTitle = this.pageTitles[this.router.url] ?? '';

    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe((e: any) => {
        this.pageTitle = this.pageTitles[e.urlAfterRedirects] ?? '';
      });
  }

  private formatRole(role: string): string {
    return role.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }

  onLogout(): void {
    this.authService.logout();
  }
}
