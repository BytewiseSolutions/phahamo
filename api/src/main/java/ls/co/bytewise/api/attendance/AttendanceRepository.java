package ls.co.bytewise.api.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByStudentIdAndLessonIdAndSchoolId(Long studentId, Long lessonId, Long schoolId);

    List<Attendance> findAllByLessonIdAndSchoolId(Long lessonId, Long schoolId);
}