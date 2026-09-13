package ls.co.bytewise.api.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class StudentDto extends BaseDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    private Instant dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private String admissionNumber;

    private StudentStatus status;

    private Long schoolClassId;

    private String currentClassName;

    public StudentDto(Student student) {
        super(student);
        this.firstName = student.getFirstName();
        this.lastName = student.getLastName();
        this.dateOfBirth = student.getDateOfBirth();
        this.gender = student.getGender();
        this.admissionNumber = student.getAdmissionNumber();
        this.status = student.getStatus();

        if (student.getCurrentClass() != null) {
            this.schoolClassId = student.getCurrentClass().getId();
            this.currentClassName = student.getCurrentClass().getName();
        }
    }
}