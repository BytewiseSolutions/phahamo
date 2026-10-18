package ls.co.bytewise.api.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentReportRowDto {
    private String className;
    private Long totalStudents;
}