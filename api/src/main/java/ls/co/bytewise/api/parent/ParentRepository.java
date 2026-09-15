package ls.co.bytewise.api.parent;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {

    Optional<Parent> findByIdAndSchoolId(Long id, Long schoolId);

    @Query("""
        SELECT p FROM Parent p
        WHERE p.school.id = :schoolId
        AND LOWER(p.email) LIKE LOWER(CONCAT('%', :email, '%'))
        """)
    List<Parent> searchByEmail(
            @Param("schoolId") Long schoolId,
            @Param("email") String email,
            Pageable pageable
    );

    @Query("""
        SELECT p FROM Parent p
        WHERE p.school.id = :schoolId
        AND p.phone LIKE CONCAT('%', :phone, '%')
        """)
    List<Parent> searchByPhone(
            @Param("schoolId") Long schoolId,
            @Param("phone") String phone,
            Pageable pageable
    );
}