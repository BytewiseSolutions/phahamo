package ls.co.bytewise.api.teacher;

import jakarta.validation.constraints.NotNull;
import ls.co.bytewise.api.common.BaseDto;
import ls.co.bytewise.api.common.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TeacherDto extends BaseDto {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Status status;

    private String qualifications;
    private String employeeNumber;
    private LocalDate dateJoined;
    private String specialization;

    @NotNull
    private EmploymentType employmentType;

    public TeacherDto(Teacher teacher) {
        super(teacher);

        if (teacher.getUser() != null) {
            this.userId = teacher.getUser().getId();
            this.firstName = teacher.getUser().getFirstName();
            this.lastName = teacher.getUser().getLastName();
            this.email = teacher.getUser().getEmail();
            this.phone = teacher.getUser().getPhone();
            this.status = teacher.getUser().getStatus();
        }

        this.qualifications = teacher.getQualifications();
        this.employeeNumber = teacher.getEmployeeNumber();
        this.dateJoined = teacher.getDateJoined();
        this.specialization = teacher.getSpecialization();
        this.employmentType = teacher.getEmploymentType();
    }
}