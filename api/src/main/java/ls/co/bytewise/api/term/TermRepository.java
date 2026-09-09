package ls.co.bytewise.api.term;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
    boolean existsByAcademicYearId(Long academicYearId);
}