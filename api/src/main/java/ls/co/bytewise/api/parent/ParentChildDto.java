package ls.co.bytewise.api.parent;

import ls.co.bytewise.api.student.Student;
import lombok.Getter;

@Getter
public class ParentChildDto {
    private final Long id;
    private final String name;
    private final String admissionNumber;
    private final Long classId;
    private final String className;

    public ParentChildDto(Student student) {
        this.id = student.getId();
        this.name = student.getUser().getFullName();
        this.admissionNumber = student.getAdmissionNumber();
        this.classId = student.getCurrentClass() == null ? null : student.getCurrentClass().getId();
        this.className = student.getCurrentClass() == null ? null : student.getCurrentClass().getName();
    }
}
