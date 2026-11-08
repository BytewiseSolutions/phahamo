package ls.co.bytewise.api.user;

import ls.co.bytewise.api.common.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import ls.co.bytewise.api.common.Status;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.roles r WHERE r = :role")
    boolean existsByRole(@Param("role") UserRole role);

    @Query("SELECT u FROM User u JOIN u.schools s WHERE s.id = :schoolId")
    List<User> findAllBySchoolId(@Param("schoolId") Long schoolId);

    @Query("""
    SELECT DISTINCT u
    FROM User u
    JOIN u.schools s
    JOIN u.roles r
    WHERE s.id = :schoolId
      AND r = :role
    """)
    List<User> findAllBySchoolIdAndRole(
            @Param("schoolId") Long schoolId,
            @Param("role") UserRole role
    );

    @Query("""
        SELECT DISTINCT u FROM User u
        LEFT JOIN u.schools s
        LEFT JOIN u.roles role
        WHERE (:schoolId IS NULL OR s.id = :schoolId)
          AND (:role IS NULL OR role = :role)
          AND (:status IS NULL OR u.status = :status)
          AND (:search IS NULL OR :search = ''
               OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%')))
        ORDER BY u.lastName, u.firstName
        """)
    Page<User> search(
            @Param("schoolId") Long schoolId,
            @Param("search") String search,
            @Param("role") UserRole role,
            @Param("status") Status status,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT u FROM User u
        JOIN u.schools s
        WHERE s.id = :schoolId
          AND u.dateOfBirth IS NOT NULL
          AND FUNCTION('MONTH', u.dateOfBirth) = :month
        """)
    List<User> findAllBySchoolIdAndBirthMonth(
            @Param("schoolId") Long schoolId,
            @Param("month") int month);
    @Query("""
    SELECT COUNT(u) FROM User u
    JOIN u.roles r
    JOIN u.schools s
    WHERE r = :role
    AND s.id = :schoolId
    AND u.status = 'ACTIVE'
   """)
    long countActiveByRoleAndSchool(@Param("role") UserRole role, @Param("schoolId") Long schoolId);

    @Query("""
    SELECT COUNT(u) FROM User u
    JOIN u.roles r
    JOIN u.schools s
    WHERE r = :role
      AND s.id = :schoolId
      AND u.status = 'ACTIVE'
      AND u.id <> :excludeUserId
""")
    long countActiveByRoleAndSchoolExcluding(
            @Param("role") UserRole role,
            @Param("schoolId") Long schoolId,
            @Param("excludeUserId") Long excludeUserId);
}
