package ls.co.bytewise.api.messaging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllBySchoolIdAndRecipientIdOrderBySentAtDesc(Long schoolId, Long recipientId);

    List<Message> findAllBySchoolIdAndSenderIdOrderBySentAtDesc(Long schoolId, Long senderId);

    @Query("""
        SELECT m FROM Message m
        WHERE m.school.id = :schoolId AND m.recipient.id = :userId
          AND NOT EXISTS (
              SELECT newer.id FROM Message newer
              WHERE newer.school.id = :schoolId
                AND newer.threadId = m.threadId
                AND newer.sentAt > m.sentAt
          )
        ORDER BY m.sentAt DESC
        """)
    Page<Message> findInboxThreads(@Param("schoolId") Long schoolId,
                                   @Param("userId") Long userId,
                                   Pageable pageable);

    @Query("""
        SELECT m FROM Message m
        WHERE m.school.id = :schoolId AND m.sender.id = :userId
          AND NOT EXISTS (
              SELECT newer.id FROM Message newer
              WHERE newer.school.id = :schoolId
                AND newer.threadId = m.threadId
                AND newer.sentAt > m.sentAt
          )
        ORDER BY m.sentAt DESC
        """)
    Page<Message> findSentThreads(@Param("schoolId") Long schoolId,
                                  @Param("userId") Long userId,
                                  Pageable pageable);

    List<Message> findAllByThreadIdAndSchoolIdOrderBySentAtAsc(String threadId, Long schoolId);

    Optional<Message> findByIdAndSchoolId(Long id, Long schoolId);

    @Query("""
            SELECT COUNT(m) FROM Message m
            WHERE m.school.id = :schoolId
              AND m.recipient.id = :userId
              AND m.readAt IS NULL
            """)
    long countUnread(@Param("schoolId") Long schoolId, @Param("userId") Long userId);
}
