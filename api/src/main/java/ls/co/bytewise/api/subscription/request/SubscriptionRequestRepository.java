package ls.co.bytewise.api.subscription.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRequestRepository extends JpaRepository<SubscriptionRequest, Long> {

    List<SubscriptionRequest> findBySchoolIdOrderByCreatedAtDesc(Long schoolId);

    List<SubscriptionRequest> findAllByOrderByCreatedAtDesc();

    List<SubscriptionRequest> findAllByStatusOrderByCreatedAtDesc(SubscriptionRequestStatus status);

    boolean existsBySchoolIdAndStatus(Long schoolId, SubscriptionRequestStatus status);

    Optional<SubscriptionRequest> findByIdAndSchoolId(Long id, Long schoolId);

}