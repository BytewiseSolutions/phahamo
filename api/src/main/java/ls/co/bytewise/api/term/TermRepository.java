package ls.co.bytewise.api.term;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {

    boolean existsByAcademicYearId(Long academicYearId);

    List<Term> findByAcademicYearId(Long academicYearId);

    List<Term> findBySchoolIdOrderByStartDateDesc(Long schoolId);

    Optional<Term> findByIdAndSchoolId(Long id, Long schoolId);
}