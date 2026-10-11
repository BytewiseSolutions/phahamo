package ls.co.bytewise.api.assessment.mark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MarkRepository extends JpaRepository<Mark, Long> {

    List<Mark> findAllByAssessmentId(Long assessmentId);

    Optional<Mark> findByAssessmentIdAndStudentId(Long assessmentId, Long studentId);

    @Query("SELECT MAX(m.score) FROM Mark m WHERE m.assessment.id = :assessmentId")
    Double findMaxScoreByAssessmentId(@Param("assessmentId") Long assessmentId);

}