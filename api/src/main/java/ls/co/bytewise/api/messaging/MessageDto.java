package ls.co.bytewise.api.messaging;

import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;

import java.time.Instant;

@Getter
public class MessageDto extends BaseDto {
    private final String threadId;
    private final Long senderId;
    private final String senderName;
    private final Long recipientId;
    private final String recipientName;
    private final String subject;
    private final String body;
    private final Instant sentAt;
    private final Instant readAt;
    private final Instant editedAt;
    private final Instant deletedAt;
    private final boolean deleted;

    public MessageDto(Message message) {
        super(message);
        this.threadId = message.getThreadId();
        this.senderId = message.getSender().getId();
        this.senderName = message.getSender().getFullName();
        this.recipientId = message.getRecipient().getId();
        this.recipientName = message.getRecipient().getFullName();
        this.subject = message.getSubject();
        this.body = message.getDeletedAt() == null ? message.getBody() : "Message deleted";
        this.sentAt = message.getSentAt();
        this.readAt = message.getReadAt();
        this.editedAt = message.getEditedAt();
        this.deletedAt = message.getDeletedAt();
        this.deleted = message.getDeletedAt() != null;
    }
}
