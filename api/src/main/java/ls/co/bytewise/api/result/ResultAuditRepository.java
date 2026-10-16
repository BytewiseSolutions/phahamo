package ls.co.bytewise.api.result;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultAuditRepository extends JpaRepository<ResultAudit, Long> {

    List<ResultAudit> findAllByTermResultIdOrderByCreatedAtDesc(Long termResultId);
}