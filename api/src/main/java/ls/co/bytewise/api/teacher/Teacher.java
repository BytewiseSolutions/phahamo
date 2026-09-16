package ls.co.bytewise.api.teacher;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import ls.co.bytewise.api.user.User;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
        name = "teacher",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_teacher_school_employee_number",
                        columnNames = {"school_id", "employee_number"}
                )
        }
)
public class Teacher extends SchoolScopedEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column
    private String qualifications;

    @Column(name = "employee_number", nullable = false)
    private String employeeNumber;

    @Column(name = "date_joined")
    private LocalDate dateJoined;

    @Column
    private String specialization;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false)
    private EmploymentType employmentType;
}