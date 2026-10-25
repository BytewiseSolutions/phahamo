package ls.co.bytewise.api.timetable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    List<Timetable> findAllBySchoolId(Long schoolId);

    List<Timetable> findAllByAssignment_Teacher_IdAndSchoolId(Long teacherId, Long schoolId);

    List<Timetable> findAllByAssignment_SchoolClass_IdAndSchoolId(Long schoolClassId, Long schoolId);

    @Query("""
        SELECT COUNT(t) FROM Timetable t
        WHERE t.school.id = :schoolId
        AND t.dayOfWeek = :dayOfWeek
        AND t.assignment.id = :assignmentId
        AND t.id <> :excludeId
        """)
    long countSlotsForAssignmentOnDay(@Param("schoolId") Long schoolId,
                                      @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                      @Param("assignmentId") Long assignmentId,
                                      @Param("excludeId") Long excludeId);

    @Query("""
        SELECT COUNT(t) > 0 FROM Timetable t
        WHERE t.school.id = :schoolId
        AND t.dayOfWeek = :dayOfWeek
        AND t.assignment.teacher.id = :teacherId
        AND t.id <> :excludeId
        AND t.period.startTime < :endTime
        AND t.period.endTime > :startTime
        """)
    boolean existsOverlappingForTeacher(@Param("schoolId") Long schoolId,
                                        @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                        @Param("teacherId") Long teacherId,
                                        @Param("startTime") LocalTime startTime,
                                        @Param("endTime") LocalTime endTime,
                                        @Param("excludeId") Long excludeId);

    @Query("""
        SELECT COUNT(t) > 0 FROM Timetable t
        WHERE t.school.id = :schoolId
        AND t.dayOfWeek = :dayOfWeek
        AND t.assignment.schoolClass.id = :schoolClassId
        AND t.id <> :excludeId
        AND t.period.startTime < :endTime
        AND t.period.endTime > :startTime
        """)
    boolean existsOverlappingForClass(@Param("schoolId") Long schoolId,
                                      @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                      @Param("schoolClassId") Long schoolClassId,
                                      @Param("startTime") LocalTime startTime,
                                      @Param("endTime") LocalTime endTime,
                                      @Param("excludeId") Long excludeId);
}
