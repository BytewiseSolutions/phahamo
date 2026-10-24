package ls.co.bytewise.api.messaging;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageEditDto {
    private String subject;

    @NotBlank
    private String body;
}
