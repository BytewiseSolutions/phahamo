package ls.co.bytewise.api.dashboard;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ChartDataDto {
    private List<String> labels;
    private List<Double> data;
}