package ls.co.bytewise.api.school;

import ls.co.bytewise.api.schoolclass.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    boolean existsByContactEmail(String contactEmail);
}