package ls.co.bytewise.api.messaging;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageSendDto {
    @NotNull
    private Long recipientId;

    private String threadId;

    private String subject;

    @NotBlank
    private String body;
}
