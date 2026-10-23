package ls.co.bytewise.api.imports;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImportSummaryDto {
    private int totalRows;
    private int validCount;
    private int invalidCount;
    private int addedCount;
    private int skippedCount;
}