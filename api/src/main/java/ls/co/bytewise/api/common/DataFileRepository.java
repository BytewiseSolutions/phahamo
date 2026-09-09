package ls.co.bytewise.api.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataFileRepository extends JpaRepository<DataFile, Long> {
}