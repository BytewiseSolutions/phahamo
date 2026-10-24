package ls.co.bytewise.api.imports;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ImportResultDto {
    private ImportSummaryDto summary = new ImportSummaryDto();
    private List<ImportRowResultDto> validRows = new ArrayList<>();
    private List<ImportRowResultDto> invalidRows = new ArrayList<>();
}