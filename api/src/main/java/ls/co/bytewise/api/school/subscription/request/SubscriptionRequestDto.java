package ls.co.bytewise.api.school.subscription.request;

import ls.co.bytewise.api.school.Currency;
import ls.co.bytewise.api.school.subscription.SubscriptionPlan;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class SubscriptionRequestDto {

    private Long id;
    private Long schoolId;
    private String schoolName;
    private SubscriptionPlan plan;
    private BigDecimal amount;
    private Currency currency;
    private Long methodId;
    private String methodLabel;
    private Long proofFileId;
    private String proofFileName;
    private String note;
    private SubscriptionRequestStatus status;
    private String reviewNote;
    private Instant createdAt;

    public SubscriptionRequestDto(SubscriptionRequest request) {
        this.id = request.getId();
        this.schoolId = request.getSchool().getId();
        this.schoolName = request.getSchool().getName();
        this.plan = request.getPlan();
        this.amount = request.getAmount();
        this.currency = request.getCurrency();
        if (request.getMethod() != null) {
            this.methodId = request.getMethod().getId();
            this.methodLabel = request.getMethod().getLabel();
        }
        if (request.getProofFile() != null) {
            this.proofFileId = request.getProofFile().getId();
            this.proofFileName = request.getProofFile().getName();
        }
        this.note = request.getNote();
        this.status = request.getStatus();
        this.reviewNote = request.getReviewNote();
        this.createdAt = request.getCreatedAt();
    }
}