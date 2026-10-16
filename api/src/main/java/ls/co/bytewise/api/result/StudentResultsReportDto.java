package ls.co.bytewise.api.result;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentResultsReportDto {

    private Long studentId;
    private String firstName;
    private String lastName;
    private String admissionNumber;
    private Long termId;
    private String termName;
    private Double averageMark;
    private String overallGrade;
    private String overallRemark;
    private List<TermResultDto> subjects;
}