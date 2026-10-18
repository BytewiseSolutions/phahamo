package ls.co.bytewise.api.result;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
    SELECT r.subject.name, 
           COUNT(r),
           AVG(r.finalMark),
           SUM(CASE WHEN r.finalMark >= 50 THEN 1 ELSE 0 END),
           MAX(r.finalMark)
    FROM TermResult r
    WHERE r.school.id = :schoolId AND r.term.id = :termId AND r.status = 'FINALISED'
    GROUP BY r.subject.id, r.subject.name
    ORDER BY r.subject.name
""")
    List<Object[]> getAcademicReport(@Param("schoolId") Long schoolId, @Param("termId") Long termId);

    long countBySchoolClassIdAndTermIdAndSchoolIdAndStatus(
            Long classId, Long termId, Long schoolId, TermResultStatus status);
}