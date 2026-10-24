package ls.co.bytewise.api.studentfee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentFeePaymentRepository extends JpaRepository<StudentFeePayment, Long> {
    Optional<StudentFeePayment> findByIdAndAccount_SchoolId(Long id, Long schoolId);

    Page<StudentFeePayment> findAllBySchoolIdOrderByPaidAtDescIdDesc(Long schoolId, Pageable pageable);
}
