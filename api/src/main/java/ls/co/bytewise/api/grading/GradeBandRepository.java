package ls.co.bytewise.api.grading;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeBandRepository extends JpaRepository<GradeBand, Long> {

    List<GradeBand> findAllBySchoolIdOrderByMinScoreDesc(Long schoolId);

    boolean existsBySchoolIdAndGrade(Long schoolId, String grade);

    boolean existsBySchoolIdAndGradeAndIdNot(Long schoolId, String grade, Long excludeId);
}