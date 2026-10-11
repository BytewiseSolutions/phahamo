package ls.co.bytewise.api.assessment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ls.co.bytewise.api.common.BaseDto;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AssessmentDto extends BaseDto {

    private Long assignmentId;
    private Long subjectId;
    private String subjectName;
    private Long classId;
    private String className;
    private Long academicYearId;
    private String academicYearName;
    private Long termId;
    private String termName;
    private Long teacherId;
    private String teacherName;

    private String name;
    private Double maxMark;
    private Integer weight;
    private LocalDate date;

    public AssessmentDto(Assessment assessment) {
        super(assessment);

        var assignment = assessment.getAssignment();
        this.assignmentId = assignment.getId();
        this.subjectId = assignment.getSubject().getId();
        this.subjectName = assignment.getSubject().getName();
        this.classId = assignment.getSchoolClass().getId();
        this.className = assignment.getSchoolClass().getName();
        this.academicYearId = assignment.getAcademicYear().getId();
        this.academicYearName = assignment.getAcademicYear().getName();

        if (assignment.getTeacher() != null) {
            this.teacherId = assignment.getTeacher().getId();
            this.teacherName = assignment.getTeacher().getFullName();
        }

        this.termId = assessment.getTerm().getId();
        this.termName = assessment.getTerm().getName();

        this.name = assessment.getName();
        this.maxMark = assessment.getMaxMark();
        this.weight = assessment.getWeight();
        this.date = assessment.getDate();
    }
}