package ls.co.bytewise.api.studentfee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;
import java.time.Instant;

@Repository
public interface StudentFeeAccountRepository extends JpaRepository<StudentFeeAccount, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM StudentFeeAccount a WHERE a.id = :id AND a.school.id = :schoolId")
    Optional<StudentFeeAccount> findByIdAndSchoolIdForUpdate(
            @Param("id") Long id,
            @Param("schoolId") Long schoolId);

    List<StudentFeeAccount> findAllBySchoolId(Long schoolId);

    List<StudentFeeAccount> findAllByTermIdAndSchoolId(Long termId, Long schoolId);

    List<StudentFeeAccount> findAllBySchoolClassIdAndTermIdAndSchoolId(
            Long classId, Long termId, Long schoolId);

    @Query("""
        SELECT a FROM StudentFeeAccount a
        WHERE a.schoolClass.id = :classId
          AND a.term.id = :termId
          AND a.school.id = :schoolId
          AND (:status = 'all'
               OR (:status = 'owing' AND a.totalFees - a.totalAdjustments - a.amountPaid > 0)
               OR (:status = 'paid' AND a.totalFees - a.totalAdjustments - a.amountPaid <= 0))
        ORDER BY a.student.user.lastName, a.student.user.firstName, a.student.admissionNumber
        """)
    Page<StudentFeeAccount> searchClass(
            @Param("classId") Long classId,
            @Param("termId") Long termId,
            @Param("schoolId") Long schoolId,
            @Param("status") String status,
            Pageable pageable);

    Optional<StudentFeeAccount> findByStudentIdAndTermIdAndSchoolId(
            Long studentId, Long termId, Long schoolId);

    List<StudentFeeAccount> findAllByStudentIdAndSchoolId(Long studentId, Long schoolId);

    Optional<StudentFeeAccount> findByIdAndSchoolId(Long id, Long schoolId);

    @Query("""
            SELECT a FROM StudentFeeAccount a
            WHERE a.student.id = :studentId
              AND a.school.id = :schoolId
              AND a.term.startDate < :termStart
            ORDER BY a.term.startDate ASC
            """)
    List<StudentFeeAccount> findPriorAccounts(
            @Param("studentId") Long studentId,
            @Param("schoolId") Long schoolId,
            @Param("termStart") Instant termStart);

    @Query("""
    SELECT COALESCE(SUM(a.totalFees - a.totalAdjustments), 0), COALESCE(SUM(a.amountPaid), 0)
    FROM StudentFeeAccount a
    WHERE a.school.id = :schoolId
    AND a.term.id = :termId
""")
    List<Object[]> sumFeesForTerm(@Param("schoolId") Long schoolId, @Param("termId") Long termId);

    @Query("""
    SELECT a.schoolClass.name,
           SUM(a.totalFees - a.totalAdjustments),
           SUM(a.amountPaid),
           SUM(a.totalFees - a.totalAdjustments - a.amountPaid)
    FROM StudentFeeAccount a
    WHERE a.school.id = :schoolId AND a.term.id = :termId
    GROUP BY a.schoolClass.id, a.schoolClass.name
    ORDER BY a.schoolClass.name
""")
    List<Object[]> getFinancialReport(@Param("schoolId") Long schoolId, @Param("termId") Long termId);
}
