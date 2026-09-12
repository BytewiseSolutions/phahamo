package ls.co.bytewise.api.student.status;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignClassRequest {

    @NotNull(message = "Class is required")
    private Long schoolClassId;
}