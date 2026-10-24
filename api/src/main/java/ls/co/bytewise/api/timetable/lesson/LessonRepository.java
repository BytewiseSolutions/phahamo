package ls.co.bytewise.api.timetable.lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    Optional<Lesson> findByTimetable_IdAndDateAndSchoolId(Long timetableId, LocalDate date, Long schoolId);

    List<Lesson> findAllByTimetable_Assignment_Teacher_IdAndDateBetweenAndSchoolIdOrderByDateAscTimetable_Period_StartTimeAsc(
            Long teacherId, LocalDate startDate, LocalDate endDate, Long schoolId);

    List<Lesson> findAllByTimetable_Assignment_Teacher_IdAndDateAndSchoolIdOrderByTimetable_Period_StartTimeAsc(
            Long teacherId, LocalDate date, Long schoolId);

    List<Lesson> findAllBySubstituteTeacher_IdAndDateAndSchoolIdOrderByTimetable_Period_StartTimeAsc(
            Long teacherId, LocalDate date, Long schoolId);

    List<Lesson> findAllBySubstituteTeacher_IdAndDateBetweenAndSchoolIdOrderByDateAscTimetable_Period_StartTimeAsc(
            Long teacherId, LocalDate startDate, LocalDate endDate, Long schoolId);

    @Query("""
            SELECT COUNT(l) > 0 FROM Lesson l
            WHERE l.school.id = :schoolId
            AND l.date = :date
            AND l.id <> :excludeLessonId
            AND l.substituteTeacher.id = :teacherId
            AND l.timetable.period.startTime < :endTime
            AND l.timetable.period.endTime > :startTime
    """)
    boolean existsOverlappingSubstitution(
            @Param("schoolId") Long schoolId,
            @Param("date") LocalDate date,
            @Param("teacherId") Long teacherId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeLessonId") Long excludeLessonId);

    List<Lesson> findAllByTimetable_Assignment_SchoolClass_IdAndDateBetweenAndSchoolIdOrderByDateAscTimetable_Period_StartTimeAsc(
            Long schoolClassId, LocalDate startDate, LocalDate endDate, Long schoolId);

    List<Lesson> findAllByStatusAndDateLessThanEqual(LessonStatus status, LocalDate date);

    boolean existsByTimetableIdAndDateAndStatus(Long timetableId, LocalDate date, LessonStatus status);

    boolean existsByTimetable_IdAndDateAndSchoolId(Long timetableId, LocalDate date, Long schoolId);

    long countBySchoolIdAndDateAndStatus(Long schoolId, LocalDate date, LessonStatus status);
}
