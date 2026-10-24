package ls.co.bytewise.api.studentfee.reminder;

import ls.co.bytewise.api.studentfee.BulkFeeEmailResultDto;
import ls.co.bytewise.api.studentfee.SendReminderRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fee-reminders")
@Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_FINANCE_OFFICER"})
public class FeeReminderController {

    private final FeeReminderService feeReminderService;

    public FeeReminderController(FeeReminderService feeReminderService) {
        this.feeReminderService = feeReminderService;
    }

    @GetMapping("/outstanding")
    public List<OutstandingStudentDto> outstanding(@RequestParam Long termId) {
        return feeReminderService.getOutstanding(termId);
    }

    @PostMapping("/send-all")
    public BulkFeeEmailResultDto sendAll(@RequestParam Long termId) {
        return feeReminderService.sendToAll(termId);
    }

    @PostMapping("/send-selected")
    public BulkFeeEmailResultDto sendSelected(@RequestBody SendReminderRequest request) {
        return feeReminderService.sendToSelected(request.getAccountIds());
    }
}
