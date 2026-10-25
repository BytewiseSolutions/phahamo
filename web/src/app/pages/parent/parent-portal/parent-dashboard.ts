import { Announcement } from '../../announcements/announcement';
import { ParentDashboardChild } from './parent-dashboard-child';

export interface ParentDashboard {
  children: ParentDashboardChild[];
  announcements: Announcement[];
}
