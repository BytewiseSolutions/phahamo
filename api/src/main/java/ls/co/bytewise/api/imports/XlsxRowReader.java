package ls.co.bytewise.api.imports;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class XlsxRowReader {
    public List<String[]> readRows(MultipartFile file) throws IOException {
        List<String[]> rows = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                int lastCell = Math.max(row.getLastCellNum(), 0);
                String[] values = new String[lastCell];
                boolean allBlank = true;

                for (int c = 0; c < lastCell; c++) {
                    Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    values[c] = cell == null ? "" : formatter.formatCellValue(cell).trim();
                    if (!values[c].isEmpty()) allBlank = false;
                }

                if (!allBlank) rows.add(values);
            }
        }

        return rows;
    }
}