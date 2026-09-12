package ls.co.bytewise.api.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByIdAndSchoolId(Long id, Long schoolId);

    List<Student> findAllBySchoolId(Long schoolId);

    boolean existsByAdmissionNumber(String admissionNumber);

    long countByAdmissionNumberStartingWith(String prefix);
}