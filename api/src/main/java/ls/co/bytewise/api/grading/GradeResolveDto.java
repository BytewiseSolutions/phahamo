package ls.co.bytewise.api.grading;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GradeResolveDto {

    private Double score;
    private String grade;
    private String remark;

    public GradeResolveDto(Double score, String grade, String remark) {
        this.score = score;
        this.grade = grade;
        this.remark = remark;
    }
}