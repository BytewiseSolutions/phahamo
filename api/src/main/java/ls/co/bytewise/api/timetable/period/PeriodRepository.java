package ls.co.bytewise.api.timetable.period;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeriodRepository extends JpaRepository<Period, Long> {
    List<Period> findAllBySchoolIdOrderByOrderIndexAsc(Long schoolId);
    boolean existsBySchoolIdAndOrderIndex(Long schoolId, Integer orderIndex);
}