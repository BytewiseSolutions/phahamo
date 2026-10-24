package ls.co.bytewise.api.user;

import ls.co.bytewise.api.common.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

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