package ls.co.bytewise.api.studentfee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentFeeAccountRepository extends JpaRepository<StudentFeeAccount, Long> {

    List<StudentFeeAccount> findAllBySchoolId(Long schoolId);

    List<StudentFeeAccount> findAllByTermIdAndSchoolId(Long termId, Long schoolId);

    List<StudentFeeAccount> findAllBySchoolClassIdAndTermIdAndSchoolId(
            Long classId, Long termId, Long schoolId);

    Optional<StudentFeeAccount> findByStudentIdAndTermIdAndSchoolId(
            Long studentId, Long termId, Long schoolId);

    Optional<StudentFeeAccount> findByIdAndSchoolId(Long id, Long schoolId);
}