package ls.co.bytewise.api.fee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnualFeeRepository extends JpaRepository<AnnualFee, Long> {

    List<AnnualFee> findAllBySchoolId(Long schoolId);

    List<AnnualFee> findAllBySchoolClassIdAndSchoolId(Long classId, Long schoolId);

    List<AnnualFee> findAllByAcademicYearIdAndSchoolId(Long academicYearId, Long schoolId);

    List<AnnualFee> findAllBySchoolClassIdAndAcademicYearIdAndSchoolId(
            Long classId, Long academicYearId, Long schoolId);

    Optional<AnnualFee> findByIdAndSchoolId(Long id, Long schoolId);
}