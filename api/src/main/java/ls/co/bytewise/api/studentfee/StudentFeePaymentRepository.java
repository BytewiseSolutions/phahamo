package ls.co.bytewise.api.studentfee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentFeePaymentRepository extends JpaRepository<StudentFeePayment, Long> {

    List<StudentFeePayment> findAllByAccountIdOrderByPaidAtDesc(Long accountId);
    Optional<StudentFeePayment> findByIdAndAccount_SchoolId(Long id, Long schoolId);
}