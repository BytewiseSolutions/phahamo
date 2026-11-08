package ls.co.bytewise.api.studentfee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.time.Instant;
import java.util.List;
import org.springframework.data.repository.query.Param;

@Repository
public interface StudentFeePaymentRepository extends JpaRepository<StudentFeePayment, Long> {
    List<StudentFeePayment> findAllBySchoolId(Long schoolId);
    Optional<StudentFeePayment> findByIdAndAccount_SchoolId(Long id, Long schoolId);

    @org.springframework.data.jpa.repository.Query("""
            SELECT p FROM StudentFeePayment p
            WHERE p.account.id = :accountId AND p.amount = :amount
            ORDER BY p.paidAt DESC
            """)
    List<StudentFeePayment> findAllByAccountIdAndAmountOrderByPaidAtDesc(
            @org.springframework.data.repository.query.Param("accountId") Long accountId,
            @org.springframework.data.repository.query.Param("amount") Double amount);

    Page<StudentFeePayment> findAllBySchoolIdOrderByPaidAtDescIdDesc(Long schoolId, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("""
            SELECT p FROM StudentFeePayment p
            WHERE p.account.school.id = :schoolId
              AND (:search IS NULL OR :search = ''
                   OR LOWER(p.account.student.user.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(p.account.student.user.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(p.account.student.admissionNumber) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(p.reference) LIKE LOWER(CONCAT('%', :search, '%')))
            ORDER BY p.paidAt DESC, p.id DESC
            """)
    Page<StudentFeePayment> searchBySchoolId(@Param("schoolId") Long schoolId,
                                             @Param("search") String search,
                                             Pageable pageable);

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
