package ls.co.bytewise.api.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeeChartDto {
    private Double expected;
    private Double paid;
}