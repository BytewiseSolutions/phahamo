package ls.co.bytewise.api.teacher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    List<Teacher> findAllBySchoolId(Long schoolId);

    Optional<Teacher> findByIdAndSchoolId(Long id, Long schoolId);

    Optional<Teacher> findByUserId(Long userId);

    long countBySchoolId(Long schoolId);

    Optional<Teacher> findTopBySchoolIdOrderByEmployeeNumberDesc(Long schoolId);
}