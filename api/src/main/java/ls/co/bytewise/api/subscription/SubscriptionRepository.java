package ls.co.bytewise.api.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findBySchoolId(Long schoolId);

    List<Subscription> findAllByOrderByEndsAtAsc();

    List<Subscription> findAllByStatusInAndEndsAtBefore(
            List<SubscriptionStatus> statuses,
            Instant cutoff
    );

    @Query("""
        SELECT s FROM Subscription s
        WHERE (:status IS NULL OR s.status = :status)
          AND (:query IS NULL OR LOWER(s.school.name) LIKE LOWER(CONCAT('%', :query, '%')))
        ORDER BY s.endsAt ASC
    """)
    List<Subscription> search(
            @Param("query") String query,
            @Param("status") SubscriptionStatus status
    );
}