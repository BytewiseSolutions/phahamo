package ls.co.bytewise.api.subject;

import jakarta.validation.constraints.NotBlank;
import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubjectDto extends BaseDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Code is required")
    private String code;

    public SubjectDto(Subject subject) {
        super(subject);
        this.name = subject.getName();
        this.code = subject.getCode();
    }
}