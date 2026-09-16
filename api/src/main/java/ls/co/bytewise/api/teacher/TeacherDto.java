package ls.co.bytewise.api.teacher;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ls.co.bytewise.api.common.BaseDto;
import ls.co.bytewise.api.user.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TeacherDto extends BaseDto {

    @Valid
    private UserDto user;

    private String qualifications;

    private String employeeNumber;

    private LocalDate dateJoined;

    private String specialization;

    @NotNull
    private EmploymentType employmentType;

    public TeacherDto(Teacher teacher) {
        super(teacher);

        this.user = new UserDto(teacher.getUser());
        this.qualifications = teacher.getQualifications();
        this.employeeNumber = teacher.getEmployeeNumber();
        this.dateJoined = teacher.getDateJoined();
        this.specialization = teacher.getSpecialization();
        this.employmentType = teacher.getEmploymentType();
    }
}