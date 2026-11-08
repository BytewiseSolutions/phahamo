package ls.co.bytewise.api.legal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface LegalDocumentRepository extends JpaRepository<LegalDocument, Long> {
    Optional<LegalDocument> findTopByDocumentTypeAndEffectiveAtLessThanEqualOrderByEffectiveAtDesc(
            LegalDocumentType documentType, Instant now);

    List<LegalDocument> findAllByOrderByEffectiveAtDesc();
}
