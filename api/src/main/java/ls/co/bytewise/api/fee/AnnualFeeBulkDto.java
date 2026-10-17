package ls.co.bytewise.api.fee;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnnualFeeBulkDto {

    private List<Long> classIds;
    private Long academicYearId;
    private String name;
    private Double amount;
    private String description;
}