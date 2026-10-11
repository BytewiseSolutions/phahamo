package ls.co.bytewise.api.assessment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    List<Assessment> findAllBySchoolIdOrderByDateDesc(Long schoolId);

    List<Assessment> findAllByAssignmentIdAndSchoolIdOrderByDateDesc(Long assignmentId, Long schoolId);

    List<Assessment> findAllByAssignment_Teacher_IdAndSchoolIdOrderByDateDesc(Long teacherId, Long schoolId);

    List<Assessment> findAllByTermIdAndSchoolIdOrderByDateDesc(Long termId, Long schoolId);

    Optional<Assessment> findByIdAndSchoolId(Long id, Long schoolId);
}