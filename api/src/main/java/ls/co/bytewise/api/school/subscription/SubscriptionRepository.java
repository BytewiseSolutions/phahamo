package ls.co.bytewise.api.school.subscription;

import org.springframework.data.jpa.repository.JpaRepository;

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
}