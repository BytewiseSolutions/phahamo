import { AudienceType } from './audience-type';
import { AnnouncementStatus } from './announcement-status';

export interface Announcement {
  id: number;
  title: string;
  body: string;
  status: AnnouncementStatus;
  audienceType: AudienceType;
  targetClassId?: number;
  targetClassName?: string;
  targetRole?: string;
  targetUserId?: number;
  targetUserName?: string;
  publishAt: string;
  expiresAt?: string;
  authorId: number;
  authorName: string;
  createdAt: string;
}
