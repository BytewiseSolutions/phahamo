package ls.co.bytewise.api.result.remark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RemarkRepository extends JpaRepository<Remark, Long> {

    Optional<Remark> findByStudentIdAndTermIdAndSchoolId(Long studentId, Long termId, Long schoolId);

    List<Remark> findAllByTermIdAndSchoolId(Long termId, Long schoolId);
}