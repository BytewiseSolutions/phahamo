package ls.co.bytewise.api.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByStudentIdAndSchoolClassIdAndDateAndSchoolId(
            Long studentId, Long schoolClassId, LocalDate date, Long schoolId);

    List<Attendance> findAllBySchoolClassIdAndDateAndSchoolId(
            Long schoolClassId, LocalDate date, Long schoolId);

    boolean existsByStudentIdAndDateAndSchoolId(Long studentId, LocalDate date, Long schoolId);
}