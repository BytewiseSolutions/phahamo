import { AudienceType, AnnouncementStatus } from './announcement';

export interface AnnouncementPayload {
  title: string;
  body: string;
  status: AnnouncementStatus;
  audienceType: AudienceType;
  targetClassId?: number;
  targetRole?: string;
  targetUserId?: number;
  publishAt?: string;
  expiresAt?: string;
}
