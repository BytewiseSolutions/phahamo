package ls.co.bytewise.api.student;

import lombok.Getter;

@Getter
public class StudentDuplicateDto {
    private final Long id;
    private final Long userId;
    private final String name;
    private final String admissionNumber;
    private final String className;
    private final StudentStatus status;

    public StudentDuplicateDto(Student student) {
        this.id = student.getId();
        this.userId = student.getUser().getId();
        this.name = student.getUser().getFullName();
        this.admissionNumber = student.getAdmissionNumber();
        this.className = student.getCurrentClass() == null ? null : student.getCurrentClass().getName();
        this.status = student.getStatus();
    }
}
