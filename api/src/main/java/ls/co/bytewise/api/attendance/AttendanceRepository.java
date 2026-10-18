package ls.co.bytewise.api.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByStudentIdAndLessonIdAndSchoolId(Long studentId, Long lessonId, Long schoolId);

    List<Attendance> findAllByLessonIdAndSchoolId(Long lessonId, Long schoolId);

    @Query("""
    SELECT a FROM Attendance a
    WHERE a.school.id = :schoolId
      AND a.date >= :fromDate
      AND a.date <= :toDate
      AND (:classId IS NULL OR a.schoolClass.id = :classId)
      AND (:subjectId IS NULL OR a.lesson.timetable.assignment.subject.id = :subjectId)
      AND (:studentId IS NULL OR a.student.id = :studentId)
    ORDER BY a.date DESC, a.lesson.timetable.period.startTime DESC
    """)
    List<Attendance> findForReport(
            @Param("schoolId") Long schoolId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("classId") Long classId,
            @Param("subjectId") Long subjectId,
            @Param("studentId") Long studentId
    );

    @Query("""
    SELECT a.status, COUNT(a)
    FROM Attendance a
    WHERE a.school.id = :schoolId
    AND a.date BETWEEN :startDate AND :endDate
    GROUP BY a.status
""")
    List<Object[]> countAttendanceByStatusForSchool(
            @Param("schoolId") Long schoolId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT 
        COALESCE(COUNT(a.id), 0), 
        COALESCE(SUM(CASE WHEN a.status = :presentStatus THEN 1 ELSE 0 END), 0)
    FROM Attendance a
    WHERE a.school.id = :schoolId
    AND a.date BETWEEN :startDate AND :endDate
    AND (:studentId IS NULL OR a.student.id = :studentId)
""")
    List<Object[]> getAttendanceSummary(
            @Param("schoolId") Long schoolId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("studentId") Long studentId,
            @Param("presentStatus") AttendanceStatus presentStatus
    );

    @Query("""
    SELECT a.schoolClass.name, 
           COUNT(a),
           SUM(CASE WHEN a.status = ls.co.bytewise.api.attendance.AttendanceStatus.PRESENT THEN 1 ELSE 0 END),
           SUM(CASE WHEN a.status = ls.co.bytewise.api.attendance.AttendanceStatus.ABSENT THEN 1 ELSE 0 END),
           SUM(CASE WHEN a.status = ls.co.bytewise.api.attendance.AttendanceStatus.LATE THEN 1 ELSE 0 END)
    FROM Attendance a
    WHERE a.school.id = :schoolId AND a.date BETWEEN :startDate AND :endDate
    GROUP BY a.schoolClass.id, a.schoolClass.name
    ORDER BY a.schoolClass.name
""")
    List<Object[]> getAttendanceByClass(
            @Param("schoolId") Long schoolId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}