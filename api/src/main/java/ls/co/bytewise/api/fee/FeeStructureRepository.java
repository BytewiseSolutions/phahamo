package ls.co.bytewise.api.fee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {

    List<FeeStructure> findAllBySchoolId(Long schoolId);

    List<FeeStructure> findAllBySchoolClassIdAndSchoolId(Long classId, Long schoolId);

    List<FeeStructure> findAllByTermIdAndSchoolId(Long termId, Long schoolId);

    List<FeeStructure> findAllBySchoolClassIdAndTermIdAndSchoolId(
            Long classId, Long termId, Long schoolId);

    Optional<FeeStructure> findByIdAndSchoolId(Long id, Long schoolId);
}