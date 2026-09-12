package ls.co.bytewise.api.subject.subjectclassassignment;

import jakarta.validation.constraints.NotNull;
import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubjectClassAssignmentDto extends BaseDto {

    @NotNull(message = "Academic year is required")
    private Long academicYearId;

    private String academicYearName;

    @NotNull(message = "Subject is required")
    private Long subjectId;

    private String subjectName;

    @NotNull(message = "Class is required")
    private Long schoolClassId;

    private String schoolClassName;

    private Long teacherId;

    private String teacherName;

    public SubjectClassAssignmentDto(SubjectClassAssignment assignment) {
        super(assignment);
        this.academicYearId = assignment.getAcademicYear().getId();
        this.academicYearName = assignment.getAcademicYear().getName();
        this.subjectId = assignment.getSubject().getId();
        this.subjectName = assignment.getSubject().getName();
        this.schoolClassId = assignment.getSchoolClass().getId();
        this.schoolClassName = assignment.getSchoolClass().getName();

        if (assignment.getTeacher() != null) {
            this.teacherId = assignment.getTeacher().getId();
            this.teacherName = assignment.getTeacher().getFullName();
        }
    }
}