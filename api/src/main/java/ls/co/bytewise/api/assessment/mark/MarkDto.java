package ls.co.bytewise.api.assessment.mark;

import ls.co.bytewise.api.common.BaseDto;
import ls.co.bytewise.api.student.Student;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MarkDto extends BaseDto {

    private Long markId;
    private Long studentId;
    private String studentFirstName;
    private String studentLastName;
    private String admissionNumber;
    private Double score;

    public static MarkDto of(Student student, Mark mark) {
        if (student == null) throw new IllegalArgumentException("student must not be null");
        if (student.getUser() == null) throw new IllegalArgumentException("student user must not be null");

        MarkDto dto = new MarkDto();
        dto.studentId = student.getId();
        dto.studentFirstName = student.getUser().getFirstName();
        dto.studentLastName = student.getUser().getLastName();
        dto.admissionNumber = student.getAdmissionNumber();

        if (mark != null) {
            dto.markId = mark.getId();
            dto.score = mark.getScore();
            dto.setId(mark.getId());
            dto.setCreatedAt(mark.getCreatedAt());
            dto.setUpdatedAt(mark.getUpdatedAt());
            if (mark.getCreatedBy() != null) {
                dto.setCreatedBy(mark.getCreatedBy().getId());
            }
            if (mark.getUpdatedBy() != null) {
                dto.setUpdatedBy(mark.getUpdatedBy().getId());
            }
        }
        return dto;
    }
}