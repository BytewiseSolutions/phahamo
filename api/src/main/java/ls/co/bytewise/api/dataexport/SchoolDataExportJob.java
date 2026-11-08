package ls.co.bytewise.api.dataexport;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SchoolDataExportJob {
    private final SchoolDataExportService service;
    public SchoolDataExportJob(SchoolDataExportService service) { this.service = service; }
    @Async public void generate(Long exportId) { service.generate(exportId); }
}
