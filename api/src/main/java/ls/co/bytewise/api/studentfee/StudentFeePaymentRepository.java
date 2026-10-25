package ls.co.bytewise.api.studentfee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.Instant;
import java.util.List;

@Repository
public interface StudentFeePaymentRepository extends JpaRepository<StudentFeePayment, Long> {
    Optional<StudentFeePayment> findByIdAndAccount_SchoolId(Long id, Long schoolId);

    Page<StudentFeePayment> findAllBySchoolIdOrderByPaidAtDescIdDesc(Long schoolId, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("""
            SELECT COUNT(p), COALESCE(SUM(p.amount), 0)
            FROM StudentFeePayment p
            WHERE p.school.id = :schoolId
            AND p.paidAt >= :from
            AND p.paidAt < :to
            AND (p.status IS NULL OR p.status <> ls.co.bytewise.api.studentfee.PaymentStatus.REVERSED)
            """)
    List<Object[]> summarizePaymentsBetween(
            @org.springframework.data.repository.query.Param("schoolId") Long schoolId,
            @org.springframework.data.repository.query.Param("from") Instant from,
            @org.springframework.data.repository.query.Param("to") Instant to);
}
