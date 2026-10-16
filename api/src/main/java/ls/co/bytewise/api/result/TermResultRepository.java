package ls.co.bytewise.api.result;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TermResultRepository extends JpaRepository<TermResult, Long> {

    List<TermResult> findAllBySchoolClassIdAndSubjectIdAndTermIdAndSchoolId(
            Long schoolClassId, Long subjectId, Long termId, Long schoolId);

    List<TermResult> findAllByStudentIdAndTermIdAndSchoolId(
            Long studentId, Long termId, Long schoolId);

    Optional<TermResult> findByStudentIdAndSubjectIdAndTermIdAndSchoolId(
            Long studentId, Long subjectId, Long termId, Long schoolId);

    List<TermResult> findAllBySchoolClassIdAndTermIdAndSchoolId(
            Long schoolClassId, Long termId, Long schoolId);

    boolean existsByStudentIdAndSubjectIdAndTermIdAndStatus(
            Long studentId, Long subjectId, Long termId, TermResultStatus status);
}