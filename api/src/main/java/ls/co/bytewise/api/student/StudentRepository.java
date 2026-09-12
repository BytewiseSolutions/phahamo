package ls.co.bytewise.api.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByAdmissionNumber(String admissionNumber);
    long countByAdmissionNumberStartingWith(String prefix);
}