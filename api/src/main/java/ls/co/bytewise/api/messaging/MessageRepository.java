package ls.co.bytewise.api.messaging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllBySchoolIdAndRecipientIdOrderBySentAtDesc(Long schoolId, Long recipientId);

    List<Message> findAllBySchoolIdAndSenderIdOrderBySentAtDesc(Long schoolId, Long senderId);

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
