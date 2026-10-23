package ls.co.bytewise.api.reportcard.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParentWithoutEmailDto {
    private Long studentId;
    private String studentFullName;
    private String admissionNumber;
    private String parentFullName;
}