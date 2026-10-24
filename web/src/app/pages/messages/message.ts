export interface Message {
  id: number;
  threadId: string;
  senderId: number;
  senderName: string;
  recipientId: number;
  recipientName: string;
  subject: string;
  body: string;
  sentAt: string;
  readAt: string | null;
  editedAt: string | null;
  deletedAt: string | null;
  deleted: boolean;
}
