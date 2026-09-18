package ls.co.bytewise.api.schoolclass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    Optional<SchoolClass> findByIdAndSchool_Id(Long id, Long schoolId);
    List<SchoolClass> findAllBySchool_Id(Long schoolId);
    boolean existsByNameAndAcademicYear_IdAndIdNot(String name, Long academicYearId, Long excludeId);
    boolean existsByNameAndAcademicYear_Id(String name, Long academicYearId);
    Optional<SchoolClass> findByClassTeacher_IdAndSchool_Id(Long classTeacherId, Long schoolId);
    boolean existsByClassTeacher_IdAndSchool_Id(Long classTeacherId, Long schoolId);
    boolean existsByClassTeacher_IdAndSchool_IdAndIdNot(Long classTeacherId, Long schoolId, Long excludeId);
}