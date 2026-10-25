package ls.co.bytewise.api.holiday;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    List<Holiday> findAllBySchoolIdOrderByStartDateAsc(Long schoolId);

    Optional<Holiday> findByIdAndSchoolId(Long id, Long schoolId);

    @Query("""
        SELECT h FROM Holiday h
        WHERE h.school.id = :schoolId
          AND h.startDate <= :toDate
          AND h.endDate >= :fromDate
        ORDER BY h.startDate ASC
        """)
    List<Holiday> findOverlapping(
            @Param("schoolId") Long schoolId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query("""
        SELECT h FROM Holiday h
        WHERE h.school.id = :schoolId
          AND h.startDate <= :date
          AND h.endDate >= :date
        ORDER BY h.startDate ASC
        """)
    List<Holiday> findBySchoolIdAndDate(
            @Param("schoolId") Long schoolId,
            @Param("date") LocalDate date);
}
