package ls.co.bytewise.api.teacher;

import ls.co.bytewise.api.common.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    List<Teacher> findAllBySchoolId(Long schoolId);

    Optional<Teacher> findByIdAndSchoolId(Long id, Long schoolId);

    Optional<Teacher> findByUserId(Long userId);

    Optional<Teacher> findByUserIdAndSchoolId(Long userId, Long schoolId);

    Optional<Teacher> findTopBySchoolIdOrderByEmployeeNumberDesc(Long schoolId);
}
