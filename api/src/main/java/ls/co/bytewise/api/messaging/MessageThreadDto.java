package ls.co.bytewise.api.messaging;

import lombok.Getter;

import java.time.Instant;

@Getter
public class MessageThreadDto {
    private final String threadId;
    private final String subject;
    private final String otherParticipantName;
    private final Long otherParticipantId;
    private final String preview;
    private final Instant lastMessageAt;
    private final boolean unread;

    public MessageThreadDto(String threadId, String subject, String otherParticipantName,
                            Long otherParticipantId, String preview, Instant lastMessageAt,
                            boolean unread) {
        this.threadId = threadId;
        this.subject = subject;
        this.otherParticipantName = otherParticipantName;
        this.otherParticipantId = otherParticipantId;
        this.preview = preview;
        this.lastMessageAt = lastMessageAt;
        this.unread = unread;
    }
}
