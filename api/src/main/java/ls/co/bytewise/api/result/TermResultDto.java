package ls.co.bytewise.api.result;

import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class TermResultDto extends BaseDto {

    private Long studentId;
    private String studentFirstName;
    private String studentLastName;
    private String admissionNumber;

    private Long subjectId;
    private String subjectName;

    private Long classId;
    private String className;

    private Long termId;
    private String termName;

    private Double finalMark;
    private String grade;
    private String remark;

    private TermResultStatus status;
    private Instant calculatedAt;
    private Instant finalisedAt;
    private Long finalisedById;
    private String finalisedByName;

    public TermResultDto(TermResult result) {
        super(result);

        this.studentId = result.getStudent().getId();
        this.studentFirstName = result.getStudent().getUser().getFirstName();
        this.studentLastName = result.getStudent().getUser().getLastName();
        this.admissionNumber = result.getStudent().getAdmissionNumber();

        this.subjectId = result.getSubject().getId();
        this.subjectName = result.getSubject().getName();

        this.classId = result.getSchoolClass().getId();
        this.className = result.getSchoolClass().getName();

        this.termId = result.getTerm().getId();
        this.termName = result.getTerm().getName();

        this.finalMark = result.getFinalMark();
        this.grade = result.getGrade();
        this.remark = result.getRemark();

        this.status = result.getStatus();
        this.calculatedAt = result.getCalculatedAt();
        this.finalisedAt = result.getFinalisedAt();
        if (result.getFinalisedBy() != null) {
            this.finalisedById = result.getFinalisedBy().getId();
            this.finalisedByName = result.getFinalisedBy().getFullName();
        }
    }
}