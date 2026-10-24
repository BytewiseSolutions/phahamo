import { Component, OnInit } from '@angular/core';
import { BackendService } from '../../../util/backend.service';
import { AuthService } from '../../../util/auth.service';
import { MessageUnreadService } from '../../../util/message-unread.service';
import { ConfirmDialogService } from '../../../util/confirm-dialog.service';
import { Message } from '../message';
import { MessageThread } from '../message-thread';
import { MessageUser } from '../message-user';

type Folder = 'inbox' | 'sent';

@Component({
  selector: 'app-messages-page',
  standalone: false,
  templateUrl: './messages-page.html',
  styleUrl: './messages-page.scss'
})
export class MessagesPage implements OnInit {
  folder: Folder = 'inbox';
  inbox: MessageThread[] = [];
  sent: MessageThread[] = [];
  recipients: MessageUser[] = [];
  thread: Message[] = [];
  selectedThreadId: string | null = null;
  loading = false;
  sending = false;
  composing = false;
  errorMessage = '';
  currentUserId: number | null;
  editingMessageId: number | null = null;
  editSubject = '';
  editBody = '';

  recipientId: number | null = null;
  subject = '';
  body = '';

  constructor(
    private backend: BackendService,
    private authService: AuthService,
    private messageUnreadService: MessageUnreadService,
    private confirmDialog: ConfirmDialogService
  ) {
    this.currentUserId = this.authService.getUserId();
  }

  get replyRecipientName(): string {
    const currentUserId = this.authService.getUserId();
    const message = this.thread[this.thread.length - 1];
    if (!message || currentUserId == null) return '';
    return message.senderId === currentUserId ? message.recipientName : message.senderName;
  }

  ngOnInit(): void {
    this.loadRecipients();
    this.loadFolder('inbox');
  }

  get visibleThreads(): MessageThread[] {
    return this.folder === 'inbox' ? this.inbox : this.sent;
  }

  get unreadInboxCount(): number {
    return this.inbox.filter(thread => thread.unread).length;
  }

  get sentThreadCount(): number {
    return this.sent.length;
  }

  loadRecipients(): void {
    this.backend.get<MessageUser[]>('/messages/recipients').subscribe({
      next: (rows) => this.recipients = rows,
      error: () => this.errorMessage = 'Failed to load message recipients.'
    });
  }

  loadFolder(folder: Folder): void {
    this.folder = folder;
    this.loading = true;
    this.errorMessage = '';
    this.backend.get<MessageThread[]>(`/messages/${folder}`).subscribe({
      next: (rows) => {
        if (folder === 'inbox') this.inbox = rows;
        else this.sent = rows;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load messages.';
        this.loading = false;
      }
    });
  }

  openThread(thread: MessageThread): void {
    this.selectedThreadId = thread.threadId;
    this.errorMessage = '';
    this.backend.get<Message[]>(`/messages/threads/${thread.threadId}`).subscribe({
      next: (messages) => {
        this.thread = messages;
        thread.unread = false;
        this.messageUnreadService.refresh();
      },
      error: () => this.errorMessage = 'Failed to open conversation.'
    });
  }

  startCompose(): void {
    this.selectedThreadId = null;
    this.thread = [];
    this.composing = true;
    this.recipientId = null;
    this.subject = '';
    this.body = '';
    this.errorMessage = '';
  }

  reply(): void {
    if (!this.thread.length) return;
    const latest = this.thread[this.thread.length - 1];
    const currentUserId = this.authService.getUserId();
    this.composing = true;
    this.recipientId = latest.senderId === currentUserId ? latest.recipientId : latest.senderId;
    this.subject = latest.subject;
    this.body = '';
    this.errorMessage = '';
  }

  isOwnMessage(message: Message): boolean {
    return message.senderId === this.currentUserId;
  }

  editMessage(message: Message): void {
    this.editingMessageId = message.id;
    this.editSubject = message.subject;
    this.editBody = message.body;
  }

  cancelEdit(): void {
    this.editingMessageId = null;
    this.editSubject = '';
    this.editBody = '';
  }

  saveEdit(): void {
    if (!this.editingMessageId || !this.editBody.trim()) {
      this.errorMessage = 'Message is required.';
      return;
    }

    this.backend.put<Message>(`/messages/${this.editingMessageId}`, {
      subject: this.editSubject.trim(),
      body: this.editBody.trim()
    }).subscribe({
      next: (updated) => {
        this.thread = this.thread.map(message => message.id === updated.id ? updated : message);
        this.cancelEdit();
      },
      error: (err) => this.errorMessage = err.error?.message || 'Failed to edit message.'
    });
  }

  async deleteMessage(message: Message): Promise<void> {
    const confirmed = await this.confirmDialog.confirm({
      title: 'Delete Message',
      message: 'Delete this message for everyone in the conversation?',
      confirmText: 'Delete',
      danger: true
    });
    if (!confirmed) return;

    this.backend.delete<void>(`/messages/${message.id}`).subscribe({
      next: () => {
        if (this.selectedThreadId) {
          this.backend.get<Message[]>(`/messages/threads/${this.selectedThreadId}`).subscribe({
            next: (messages) => this.thread = messages
          });
        }
      },
      error: (err) => this.errorMessage = err.error?.message || 'Failed to delete message.'
    });
  }

  send(): void {
    if (!this.recipientId || !this.body.trim()) {
      this.errorMessage = 'Recipient and message are required.';
      return;
    }

    this.sending = true;
    this.errorMessage = '';
    this.backend.postSilently<Message>('/messages', {
      recipientId: this.recipientId,
      threadId: this.selectedThreadId,
      subject: this.subject.trim(),
      body: this.body.trim()
    }).subscribe({
      next: (message) => {
        this.sending = false;
        this.composing = false;
        this.selectedThreadId = message.threadId;
        this.thread = [...this.thread, message];
        this.messageUnreadService.refresh();
        this.loadFolder(this.folder);
        this.loadFolder('sent');
      },
      error: (err) => {
        this.sending = false;
        this.errorMessage = err.error?.message || 'Failed to send message.';
      }
    });
  }
}
