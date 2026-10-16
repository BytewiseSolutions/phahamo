package ls.co.bytewise.api.result;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class ResultAuditDto {

    private Long id;
    private Long termResultId;
    private Double previousMark;
    private Double newMark;
    private String reason;
    private Long changedById;
    private String changedByName;
    private Instant changedAt;

    public ResultAuditDto(ResultAudit audit) {
        this.id = audit.getId();
        this.termResultId = audit.getTermResult().getId();
        this.previousMark = audit.getPreviousMark();
        this.newMark = audit.getNewMark();
        this.reason = audit.getReason();
        if (audit.getChangedBy() != null) {
            this.changedById = audit.getChangedBy().getId();
            this.changedByName = audit.getChangedBy().getFullName();
        }
        this.changedAt = audit.getCreatedAt();
    }
}