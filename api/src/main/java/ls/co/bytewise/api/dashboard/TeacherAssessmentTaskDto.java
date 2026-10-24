package ls.co.bytewise.api.dashboard;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TeacherAssessmentTaskDto {
    private Long id;
    private String name;
    private String subjectName;
    private String className;
    private LocalDate date;
    private long missingMarks;

    public TeacherAssessmentTaskDto(Long id, String name, String subjectName,
                                    String className, LocalDate date, long missingMarks) {
        this.id = id;
        this.name = name;
        this.subjectName = subjectName;
        this.className = className;
        this.date = date;
        this.missingMarks = missingMarks;
    }
}
