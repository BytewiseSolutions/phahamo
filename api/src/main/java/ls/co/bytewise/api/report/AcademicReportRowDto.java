package ls.co.bytewise.api.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcademicReportRowDto {
    private String subjectName;
    private Long totalStudents;
    private Double averageMark;
    private Double passRate;
    private Double highestMark;
}