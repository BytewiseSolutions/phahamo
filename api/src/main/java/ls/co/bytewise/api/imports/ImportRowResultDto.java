package ls.co.bytewise.api.imports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportRowResultDto {
    private int rowNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String reason;
    private String generatedId;
}