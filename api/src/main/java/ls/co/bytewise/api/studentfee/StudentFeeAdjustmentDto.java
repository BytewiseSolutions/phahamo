package ls.co.bytewise.api.studentfee;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class StudentFeeAdjustmentDto {

    private Long id;
    private AdjustmentType type;
    private Double amount;
    private String reason;
    private Long approvedById;
    private String approvedByName;
    private Instant appliedAt;

    public StudentFeeAdjustmentDto(StudentFeeAdjustment adj) {
        this.id = adj.getId();
        this.type = adj.getType();
        this.amount = adj.getAmount();
        this.reason = adj.getReason();
        if (adj.getApprovedBy() != null) {
            this.approvedById = adj.getApprovedBy().getId();
            this.approvedByName = adj.getApprovedBy().getFullName();
        }
        this.appliedAt = adj.getAppliedAt();
    }
}
