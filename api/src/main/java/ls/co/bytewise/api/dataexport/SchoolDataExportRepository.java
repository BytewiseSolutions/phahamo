package ls.co.bytewise.api.dataexport;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SchoolDataExportRepository extends JpaRepository<SchoolDataExport, Long> {
    Optional<SchoolDataExport> findByToken(String token);
}
