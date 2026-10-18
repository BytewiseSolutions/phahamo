package ls.co.bytewise.api.timetable.lesson;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    Optional<Lesson> findByTimetable_IdAndDateAndSchoolId(Long timetableId, LocalDate date, Long schoolId);

    List<Lesson> findAllByTimetable_Assignment_Teacher_IdAndDateBetweenAndSchoolIdOrderByDateAscTimetable_Period_StartTimeAsc(
            Long teacherId, LocalDate startDate, LocalDate endDate, Long schoolId);

    List<Lesson> findAllByTimetable_Assignment_SchoolClass_IdAndDateBetweenAndSchoolIdOrderByDateAscTimetable_Period_StartTimeAsc(
            Long schoolClassId, LocalDate startDate, LocalDate endDate, Long schoolId);

    List<Lesson> findAllByStatusAndDateLessThanEqual(LessonStatus status, LocalDate date);
    boolean existsByTimetableIdAndDateAndStatus(Long timetableId, LocalDate date, LessonStatus status);
}