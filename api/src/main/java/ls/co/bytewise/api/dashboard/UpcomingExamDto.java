package ls.co.bytewise.api.dashboard;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UpcomingExamDto {

    private Long examId;
    private String name;
    private String subjectName;
    private LocalDate date;
    private LocalDate endDate;

    public UpcomingExamDto(Long examId, String name, String subjectName, LocalDate date, LocalDate endDate) {
        this.examId = examId;
        this.name = name;
        this.subjectName = subjectName;
        this.date = date;
        this.endDate = endDate;
    }
}
