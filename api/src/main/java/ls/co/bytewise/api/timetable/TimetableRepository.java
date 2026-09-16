package ls.co.bytewise.api.timetable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    List<Timetable> findAllBySchoolId(Long schoolId);

    List<Timetable> findAllByAssignment_Teacher_IdAndSchoolId(Long teacherId, Long schoolId);

    List<Timetable> findAllByAssignment_SchoolClass_IdAndSchoolId(Long schoolClassId, Long schoolId);

    boolean existsByDayOfWeekAndPeriod_IdAndAssignment_Teacher_IdAndSchoolId(
            DayOfWeek dayOfWeek, Long periodId, Long teacherId, Long schoolId);

    boolean existsByDayOfWeekAndPeriod_IdAndAssignment_SchoolClass_IdAndSchoolId(
            DayOfWeek dayOfWeek, Long periodId, Long schoolClassId, Long schoolId);
}