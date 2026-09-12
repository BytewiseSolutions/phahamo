package ls.co.bytewise.api.subject.subjectclassassignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectClassAssignmentRepository extends JpaRepository<SubjectClassAssignment, Long> {

    List<SubjectClassAssignment> findAllBySchoolClassId(Long schoolClassId);

    List<SubjectClassAssignment> findAllBySubjectId(Long subjectId);

    boolean existsBySubjectIdAndSchoolClassIdAndAcademicYearId(Long subjectId, Long schoolClassId, Long academicYearId);

    boolean existsBySubjectIdAndSchoolClassIdAndAcademicYearIdAndIdNot(Long subjectId, Long schoolClassId, Long academicYearId, Long excludeId);

    boolean existsBySubjectId(Long subjectId);
}