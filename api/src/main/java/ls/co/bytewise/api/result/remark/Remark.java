package ls.co.bytewise.api.result.remark;

import jakarta.persistence.*;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import ls.co.bytewise.api.student.Student;
import ls.co.bytewise.api.term.Term;
import ls.co.bytewise.api.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "student_term_remark", uniqueConstraints = @UniqueConstraint(
        name = "uk_student_term_remark",
        columnNames = {"student_id", "term_id"}
))
public class Remark extends SchoolScopedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @Column(length = 2000)
    private String classTeacherRemark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_teacher_remark_by")
    private User classTeacherRemarkBy;

    private Instant classTeacherRemarkAt;

    @Column(length = 2000)
    private String principalRemark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "principal_remark_by")
    private User principalRemarkBy;

    private Instant principalRemarkAt;
}