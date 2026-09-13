package ls.co.bytewise.api.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByAdmissionNumber(String admissionNumber);

    long countByAdmissionNumberStartingWith(String prefix);

    Optional<Student> findByIdAndSchoolId(Long id, Long schoolId);

    List<Student> findAllBySchoolIdAndCurrentClassId(Long schoolId, Long currentClassId);

    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId " +
            "AND (:search IS NULL OR :search = '' " +
            "OR LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(s.admissionNumber) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Student> search(@Param("schoolId") Long schoolId, @Param("search") String search, Pageable pageable);
}