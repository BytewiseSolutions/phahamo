package ls.co.bytewise.api.student.status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentStatusChangeRepository extends JpaRepository<StudentStatusChange, Long> {
    List<StudentStatusChange> findAllByStudentIdOrderByCreatedAtDesc(Long studentId);
}
