package ls.co.bytewise.api.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("""
            SELECT a FROM AuditLog a
            WHERE a.school.id = :schoolId
              AND (:actorId IS NULL OR a.actor.id = :actorId)
              AND (:action IS NULL OR a.action = :action)
              AND (:from IS NULL OR a.createdAt >= :from)
              AND (:to IS NULL OR a.createdAt < :to)
            ORDER BY a.createdAt DESC, a.id DESC
            """)
    Page<AuditLog> search(
            @Param("schoolId") Long schoolId,
            @Param("actorId") Long actorId,
            @Param("action") AuditAction action,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable);
}
