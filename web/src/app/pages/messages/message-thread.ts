export interface MessageThread {
  threadId: string;
  subject: string;
  otherParticipantName: string;
  otherParticipantId: number;
  preview: string;
  lastMessageAt: string;
  unread: boolean;
}
