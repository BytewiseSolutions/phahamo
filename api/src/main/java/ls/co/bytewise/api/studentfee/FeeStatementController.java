package ls.co.bytewise.api.studentfee;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fee-statements")
@Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_FINANCE_OFFICER"})
public class FeeStatementController {

    private final FeeStatementService statementService;

    public FeeStatementController(FeeStatementService statementService) {
        this.statementService = statementService;
    }

    @PostMapping("/account/{accountId}/email")
    public FeeStatementResultDto emailForAccount(@PathVariable Long accountId) {
        return statementService.emailForAccount(accountId);
    }

    @PostMapping("/term/{termId}/email-all")
    public FeeStatementResultDto emailAllOutstanding(@PathVariable Long termId) {
        return statementService.emailAllOutstanding(termId);
    }
}