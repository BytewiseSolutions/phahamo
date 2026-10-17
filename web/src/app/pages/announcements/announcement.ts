export type AudienceType = 'ALL' | 'CLASS' | 'ROLE' | 'USER';
export type AnnouncementStatus = 'DRAFT' | 'PUBLISHED';

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
