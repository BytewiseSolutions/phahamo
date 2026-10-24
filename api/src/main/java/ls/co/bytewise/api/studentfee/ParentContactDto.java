package ls.co.bytewise.api.studentfee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParentContactDto {
    private String fullName;
    private String email;
    private String phone;
    private boolean hasEmail;
}