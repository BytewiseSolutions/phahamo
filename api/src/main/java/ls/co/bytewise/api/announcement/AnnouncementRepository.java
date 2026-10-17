package ls.co.bytewise.api.announcement;

import ls.co.bytewise.api.common.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findAllBySchoolIdOrderByPublishAtDesc(Long schoolId);

    @Query("""
    SELECT a FROM Announcement a
    WHERE a.school.id = :schoolId
    AND a.status = 'PUBLISHED'
    AND a.publishAt <= :now
    AND (a.expiresAt IS NULL OR a.expiresAt > :now)
    AND (
        a.audienceType = 'ALL'
        OR (a.audienceType = 'CLASS' AND a.targetClass.id IN :classIds)
        OR (a.audienceType = 'ROLE' AND a.targetRole = :role)
        OR (a.audienceType = 'USER' AND a.targetUser.id = :userId)
    )
    ORDER BY a.publishAt DESC
""")
    List<Announcement> findVisibleForUser(
            @Param("schoolId") Long schoolId,
            @Param("now") Instant now,
            @Param("classIds") List<Long> classIds,
            @Param("role") UserRole role,
            @Param("userId") Long userId
    );
}