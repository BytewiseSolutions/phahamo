package ls.co.bytewise.api.student;

import ls.co.bytewise.api.parent.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByAdmissionNumber(String admissionNumber);

    long countByAdmissionNumberStartingWith(String prefix);

    Optional<Student> findByIdAndSchoolId(Long id, Long schoolId);

    boolean existsByIdAndSchoolId(Long id, Long schoolId);

    List<Student> findByParentsId(Long parentId);

    List<Student> findAllBySchoolIdAndCurrentClassId(Long schoolId, Long currentClassId);

    @Query("""
    SELECT s
    FROM Student s
    WHERE s.school.id = :schoolId
    AND (:search IS NULL OR :search = ''
        OR LOWER(s.user.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(s.user.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(s.admissionNumber) LIKE LOWER(CONCAT('%', :search, '%'))
    )
    """)
    org.springframework.data.domain.Page<Student> search(
            @Param("schoolId") Long schoolId,
            @Param("search") String search,
            org.springframework.data.domain.Pageable pageable
    );

    @Query("""
        SELECT p FROM Student s JOIN s.parents p WHERE s.id = :studentId
        """)
    List<Parent> findParentsByStudentId(
            @Param("studentId") Long studentId
    );

    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
        FROM Student s JOIN s.parents p WHERE s.id = :studentId AND p.id = :parentId
        """)
    boolean existsParentLink(
            @Param("studentId") Long studentId,
            @Param("parentId") Long parentId
    );

    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
        FROM Student s
        JOIN s.parents p
        WHERE p.id = :parentId
        """)
    boolean existsAnyLinkForParent(
            @Param("parentId") Long parentId
    );

    Optional<Student> findByUserId(Long userId);
    long countBySchoolIdAndStatus(Long schoolId, StudentStatus status);

    @Query("""
    SELECT s.currentClass.name, COUNT(s)
    FROM Student s
    WHERE s.school.id = :schoolId AND s.status = 'ACTIVE' AND s.currentClass IS NOT NULL
    GROUP BY s.currentClass.id, s.currentClass.name
    ORDER BY s.currentClass.name
""")
    List<Object[]> countStudentsByClass(@Param("schoolId") Long schoolId);
}