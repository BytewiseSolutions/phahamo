package ls.co.bytewise.api.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AbsenceAlertLogRepository extends JpaRepository<AbsenceAlertLog, Long> {

    boolean existsByStudentIdAndAbsenceDate(Long studentId, LocalDate absenceDate);
}