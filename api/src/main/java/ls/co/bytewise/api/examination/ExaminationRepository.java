package ls.co.bytewise.api.examination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    List<Examination> findAllBySchoolIdOrderByStartDateDesc(Long schoolId);

    List<Examination> findAllByTermIdAndSchoolIdOrderByStartDateDesc(Long termId, Long schoolId);

    Optional<Examination> findByIdAndSchoolId(Long id, Long schoolId);

    @Query("""
        SELECT e FROM Examination e
        WHERE e.school.id = :schoolId
          AND e.term.id = :termId
          AND e.id <> :excludeId
          AND e.startDate <= :endDate
          AND e.endDate >= :startDate
        """)
    List<Examination> findOverlapping(
            @Param("schoolId") Long schoolId,
            @Param("termId") Long termId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") Long excludeId
    );
}