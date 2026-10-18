package ls.co.bytewise.api.subject.subjectclassassignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectClassAssignmentRepository extends JpaRepository<SubjectClassAssignment, Long> {

    List<SubjectClassAssignment> findAllBySchoolId(Long schoolId);

    List<SubjectClassAssignment> findAllBySchoolClassIdAndSchoolId(Long schoolClassId, Long schoolId);

    List<SubjectClassAssignment> findAllBySubjectIdAndSchoolId(Long subjectId, Long schoolId);

    List<SubjectClassAssignment> findAllByTeacherIdAndSchoolId(Long teacherId, Long schoolId);

    boolean existsBySubjectIdAndSchoolClassIdAndAcademicYearId(
            Long subjectId,
            Long schoolClassId,
            Long academicYearId
    );

    boolean existsBySubjectIdAndSchoolClassIdAndAcademicYearIdAndIdNot(
            Long subjectId,
            Long schoolClassId,
            Long academicYearId,
            Long excludeId
    );

    boolean existsBySubjectId(Long subjectId);
    long countByTeacherIdAndSchoolId(Long teacherId, Long schoolId);
    long countBySchoolClassIdAndSchoolId(Long classId, Long schoolId);
}