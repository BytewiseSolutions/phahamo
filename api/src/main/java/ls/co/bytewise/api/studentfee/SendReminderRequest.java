package ls.co.bytewise.api.studentfee;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SendReminderRequest {
    private List<Long> accountIds;
}