package ls.co.bytewise.api.grading;

import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GradeBandDto extends BaseDto {

    private Double minScore;
    private Double maxScore;
    private String grade;
    private String remark;

    public GradeBandDto(GradeBand band) {
        super(band);
        this.minScore = band.getMinScore();
        this.maxScore = band.getMaxScore();
        this.grade = band.getGrade();
        this.remark = band.getRemark();
    }
}