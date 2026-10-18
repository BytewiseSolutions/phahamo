package ls.co.bytewise.api.student.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findAllByBatchId(String batchId);
    List<Promotion> findAllByStudentIdOrderByPromotedAtDesc(Long studentId);
    List<Promotion> findAllBySchoolIdOrderByPromotedAtDesc(Long schoolId);
    List<Promotion> findAllBySchoolIdAndStudentIdOrderByPromotedAtDesc(Long schoolId, Long studentId);
}