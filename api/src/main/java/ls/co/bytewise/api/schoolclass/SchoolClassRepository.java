package ls.co.bytewise.api.schoolclass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    Optional<SchoolClass> findByIdAndSchool_Id(Long id, Long schoolId);
    List<SchoolClass> findAllBySchool_Id(Long schoolId);
}