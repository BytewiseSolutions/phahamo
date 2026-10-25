package ls.co.bytewise.api.audit;

import java.time.Instant;

public record AuditLogDto(
        Long id,
        AuditAction action,
        Long actorId,
        String actorName,
        String targetType,
        Long targetId,
        String summary,
        Instant occurredAt
) {
    public AuditLogDto(AuditLog log) {
        this(
                log.getId(),
                log.getAction(),
                log.getActor() != null ? log.getActor().getId() : null,
                log.getActor() != null ? log.getActor().getFullName() : "System",
                log.getTargetType(),
                log.getTargetId(),
                log.getSummary(),
                log.getCreatedAt()
        );
    }
}
