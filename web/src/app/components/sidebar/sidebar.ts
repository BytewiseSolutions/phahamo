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
];

const ROLE_LINKS: Record<string, SidebarLink[]> = {
  SCHOOL_ADMIN: [
    { label: 'Users', path: '/users', icon: 'fa-solid fa-users' },
    { label: 'Classes', path: '/classes', icon: 'fa-solid fa-chalkboard' },
    { label: 'Subjects', path: '/subjects', icon: 'fa-solid fa-book' },
    { label: 'Academic Years', path: '/academic-years', icon: 'fa-solid fa-calendar-check' },
    { label: 'Timetable', path: '/timetable', icon: 'fa-solid fa-calendar-days' },
    { label: 'Lesson Ranges', path: '/lessons/range', icon: 'fa-solid fa-calendar-week' },
    { label: 'Grades', path: '/grades', icon: 'fa-solid fa-square-poll-vertical' },
    { label: 'Results', path: '/results', icon: 'fa-solid fa-clipboard-check' },
    { label: 'Email Report Cards', path: '/report-cards/email', icon: 'fa-solid fa-envelope' },
    { label: 'Student Remarks', path: '/remarks', icon: 'fa-solid fa-comment-dots' },
    { label: 'Student Promotion', path: '/students/promote', icon: 'fa-solid fa-arrow-up-right-dots' },
    { label: 'Attendance Reports', path: '/attendance/history', icon: 'fa-solid fa-clipboard-check' },
    { label: 'Examinations', path: '/examinations', icon: 'fa-solid fa-file-pen' },
    { label: 'Grading Scale', path: '/grading-scale', icon: 'fa-solid fa-award' },
    { label: 'School Profile', path: '/school-profile', icon: 'fa-solid fa-school' },
    { label: 'Fee Structures', path: '/fees/structures', icon: 'fa-solid fa-money-bill-wave' },
    { label: 'Student Fees', path: '/student-fees', icon: 'fa-solid fa-file-invoice-dollar' },
    { label: 'Announcements', path: '/announcements', icon: 'fa-solid fa-bullhorn' },
    { label: 'Reports', path: '/reports', icon: 'fa-solid fa-chart-line' },
    { label: 'Subscriptions', path: '/subscriptions', icon: 'fa-solid fa-file-invoice-dollar' },
  ],
  PRINCIPAL: [
    { label: 'Users', path: '/users', icon: 'fa-solid fa-users' },
    { label: 'Classes', path: '/classes', icon: 'fa-solid fa-chalkboard' },
    { label: 'Subjects', path: '/subjects', icon: 'fa-solid fa-book' },
    { label: 'Academic Years', path: '/academic-years', icon: 'fa-solid fa-calendar-check' },
    { label: 'Timetable', path: '/timetable', icon: 'fa-solid fa-calendar-days' },
    { label: 'Lesson Ranges', path: '/lessons/range', icon: 'fa-solid fa-calendar-week' },
    { label: 'Grades', path: '/grades', icon: 'fa-solid fa-square-poll-vertical' },
    { label: 'Results', path: '/results', icon: 'fa-solid fa-clipboard-check' },
    { label: 'Email Report Cards', path: '/report-cards/email', icon: 'fa-solid fa-envelope' },
    { label: 'Student Remarks', path: '/remarks', icon: 'fa-solid fa-comment-dots' },
    { label: 'Student Promotion', path: '/students/promote', icon: 'fa-solid fa-arrow-up-right-dots' },
    { label: 'Attendance Reports', path: '/attendance/history', icon: 'fa-solid fa-clipboard-check' },
    { label: 'Examinations', path: '/examinations', icon: 'fa-solid fa-file-pen' },
    { label: 'Fee Structures', path: '/fees/structures', icon: 'fa-solid fa-money-bill-wave' },
    { label: 'Student Fees', path: '/student-fees', icon: 'fa-solid fa-file-invoice-dollar' },
    { label: 'Announcements', path: '/announcements', icon: 'fa-solid fa-bullhorn' },
    { label: 'Reports', path: '/reports', icon: 'fa-solid fa-chart-line' },
    { label: 'Subscriptions', path: '/subscriptions', icon: 'fa-solid fa-file-invoice-dollar' },
  ],
  TEACHER: [
    { label: 'My Classes', path: '/my-classes', icon: 'fa-solid fa-chalkboard-user' },
    { label: 'Grades', path: '/grades', icon: 'fa-solid fa-square-poll-vertical' },
    { label: 'Results', path: '/results', icon: 'fa-solid fa-clipboard-check' },
    { label: 'Email Report Cards', path: '/report-cards/email', icon: 'fa-solid fa-envelope' },
    { label: 'Student Remarks', path: '/remarks', icon: 'fa-solid fa-comment-dots' },
    { label: 'Attendance Reports', path: '/attendance/history', icon: 'fa-solid fa-clipboard-check' },
    { label: 'Timetable', path: '/timetable', icon: 'fa-solid fa-calendar-days' },
    { label: 'Lesson Ranges', path: '/lessons/range', icon: 'fa-solid fa-calendar-week' },
    { label: 'Profile', path: '/profile', icon: 'fa-solid fa-user' },
  ],
  FINANCE_OFFICER: [
    { label: 'Fees', path: '/student-fees', icon: 'fa-solid fa-money-check-dollar' },
    { label: 'Payments', path: '/student-fees/payments', icon: 'fa-solid fa-credit-card' },
    { label: 'Student Fees', path: '/student-fees', icon: 'fa-solid fa-file-invoice-dollar' },
    { label: 'Reports', path: '/reports', icon: 'fa-solid fa-chart-line' },
    { label: 'Subscriptions', path: '/subscriptions', icon: 'fa-solid fa-file-invoice-dollar' },
  ],
  PARENT: [
    { label: 'My Children', path: '/my-children', icon: 'fa-solid fa-child' },
    { label: 'Attendance', path: '/attendance', icon: 'fa-solid fa-clipboard-check' },
    { label: 'Results', path: '/results', icon: 'fa-solid fa-file-lines' },
    { label: 'Fees', path: '/fees', icon: 'fa-solid fa-money-check-dollar' },
  ],
  STUDENT: [
    { label: 'Timetable', path: '/timetable', icon: 'fa-solid fa-calendar-days' },
    { label: 'My Attendance', path: '/attendance/my', icon: 'fa-solid fa-clipboard-check' },
    { label: 'My Results', path: '/results/my', icon: 'fa-solid fa-graduation-cap' },
    { label: 'My Fees', path: '/my-fees', icon: 'fa-solid fa-file-invoice-dollar' },
  ],
  SYSTEM_ADMIN: [
    { label: 'Schools', path: '/schools', icon: 'fa-solid fa-school' },
    { label: 'School Subscriptions', path: '/admin/schools', icon: 'fa-solid fa-building-columns' },
    { label: 'Users', path: '/users', icon: 'fa-solid fa-users' },
    { label: 'Subscriptions', path: '/subscriptions', icon: 'fa-solid fa-file-invoice-dollar' },
    { label: 'Plan Prices', path: '/plan-prices', icon: 'fa-solid fa-tags' },
    { label: 'Payment Methods', path: '/payment-methods', icon: 'fa-solid fa-credit-card' },
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

  isLinkActive(link: SidebarLink): boolean {
    const currentPath = this.router.url.split('?')[0];

    if (link.path === '/student-fees') {
      return currentPath === link.path
        || (currentPath.startsWith(`${link.path}/`)
          && !currentPath.startsWith('/student-fees/payments'));
    }

    return currentPath === link.path || currentPath.startsWith(`${link.path}/`);
  }

  @HostListener('window:resize')
  onResize(): void {
    if (window.innerWidth > 768) {
      this.mobileOpen = false;
    }
  }
}
