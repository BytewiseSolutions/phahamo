import { Component, HostListener, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../../util/auth.service';

interface SidebarLink {
  label: string;
  path: string;
  icon: string;
}

const COMMON_LINKS: SidebarLink[] = [
  { label: 'Dashboard', path: '/dashboard', icon: 'fa-solid fa-gauge' },
  { label: 'Profile', path: '/profile', icon: 'fa-solid fa-user' },
];

const ROLE_LINKS: Record<string, SidebarLink[]> = {
  SCHOOL_ADMIN: [
    { label: 'Users', path: '/users', icon: 'fa-solid fa-users' },
    { label: 'Students', path: '/students', icon: 'fa-solid fa-user-graduate' },
    { label: 'Classes', path: '/classes', icon: 'fa-solid fa-chalkboard' },
    { label: 'Subjects', path: '/subjects', icon: 'fa-solid fa-book' },
    { label: 'Academic Years', path: '/academic-years', icon: 'fa-solid fa-calendar-check' },
    { label: 'Timetable', path: '/timetable', icon: 'fa-solid fa-calendar-days' },
    { label: 'Reports', path: '/reports', icon: 'fa-solid fa-chart-line' },
    { label: 'School Profile', path: '/school-profile', icon: 'fa-solid fa-school' },
  ],
  PRINCIPAL: [
    { label: 'Students', path: '/students', icon: 'fa-solid fa-user-graduate' },
    { label: 'Classes', path: '/classes', icon: 'fa-solid fa-chalkboard' },
    { label: 'Subjects', path: '/subjects', icon: 'fa-solid fa-book' },
    { label: 'Academic Years', path: '/academic-years', icon: 'fa-solid fa-calendar-check' },
    { label: 'Timetable', path: '/timetable', icon: 'fa-solid fa-calendar-days' },
    { label: 'Reports', path: '/reports', icon: 'fa-solid fa-chart-line' },
  ],
  TEACHER: [
    { label: 'My Classes', path: '/my-classes', icon: 'fa-solid fa-chalkboard-user' },
    { label: 'Attendance', path: '/attendance', icon: 'fa-solid fa-clipboard-check' },
    { label: 'Grades', path: '/grades', icon: 'fa-solid fa-square-poll-vertical' },
    { label: 'Timetable', path: '/timetable', icon: 'fa-solid fa-calendar-days' },
  ],
  FINANCE_OFFICER: [
    { label: 'Fees', path: '/fees', icon: 'fa-solid fa-money-check-dollar' },
    { label: 'Payments', path: '/payments', icon: 'fa-solid fa-credit-card' },
    { label: 'Reports', path: '/reports', icon: 'fa-solid fa-chart-line' },
  ],
  PARENT: [
    { label: 'My Children', path: '/my-children', icon: 'fa-solid fa-child' },
    { label: 'Attendance', path: '/attendance', icon: 'fa-solid fa-clipboard-check' },
    { label: 'Results', path: '/results', icon: 'fa-solid fa-file-lines' },
    { label: 'Fees', path: '/fees', icon: 'fa-solid fa-money-check-dollar' },
  ],
  STUDENT: [
    { label: 'Timetable', path: '/timetable', icon: 'fa-solid fa-calendar-days' },
    { label: 'Attendance', path: '/attendance', icon: 'fa-solid fa-clipboard-check' },
    { label: 'Results', path: '/results', icon: 'fa-solid fa-file-lines' },
    { label: 'Fees', path: '/fees', icon: 'fa-solid fa-money-check-dollar' },
  ],
  SYSTEM_ADMIN: [
    { label: 'Schools', path: '/schools', icon: 'fa-solid fa-school' },
    { label: 'Subscriptions', path: '/subscriptions', icon: 'fa-solid fa-file-invoice-dollar' },
    { label: 'Users', path: '/users', icon: 'fa-solid fa-users' },
  ],
};

@Component({
  selector: 'app-sidebar',
  standalone: false,
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss'
})
export class Sidebar implements OnInit {
  links: SidebarLink[] = [];
  roles: string[] = [];
  collapsed = false;
  mobileOpen = false;
  schoolName = '';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.roles = this.authService.getRoles();
    this.links = this.buildLinks(this.roles);
    this.schoolName = this.authService.getSchoolName();

    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.mobileOpen = false;
      }
    });
  }

  private buildLinks(roles: string[]): SidebarLink[] {
    const seenPaths = new Set<string>();
    const result: SidebarLink[] = [];

    const addAll = (links: SidebarLink[]) => {
      for (const link of links) {
        if (!seenPaths.has(link.path)) {
          seenPaths.add(link.path);
          result.push(link);
        }
      }
    };

    addAll(COMMON_LINKS);
    for (const role of roles) {
      addAll(ROLE_LINKS[role] ?? []);
    }

    return result;
  }

  toggleCollapsed(): void {
    this.collapsed = !this.collapsed;
  }

  toggleMobile(): void {
    this.mobileOpen = !this.mobileOpen;
  }

  onLogout(): void {
    this.authService.logout();
  }

  @HostListener('window:resize')
  onResize(): void {
    if (window.innerWidth > 768) {
      this.mobileOpen = false;
    }
  }
}
