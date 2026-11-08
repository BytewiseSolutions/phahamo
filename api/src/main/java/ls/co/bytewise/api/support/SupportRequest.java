package ls.co.bytewise.api.support;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportRequest {
    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Question is required")
    private String question;
}
