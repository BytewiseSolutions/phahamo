package ls.co.bytewise.api.student.status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ls.co.bytewise.api.student.StudentStatus;

@Getter
@Setter
public class ChangeStatusRequest {

    @NotNull(message = "Status is required")
    private StudentStatus status;

    @NotBlank(message = "Reason is required")
    private String reason;
}
