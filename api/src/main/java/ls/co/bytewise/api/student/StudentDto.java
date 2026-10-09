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

    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

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

        if (student.getUser() != null) {
            this.userId = student.getUser().getId();
            this.firstName = student.getUser().getFirstName();
            this.lastName = student.getUser().getLastName();
            this.email = student.getUser().getEmail();
            this.phone = student.getUser().getPhone();
        }

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