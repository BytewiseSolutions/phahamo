package ls.co.bytewise.api.student.promotion;

import ls.co.bytewise.api.common.BaseDto;
import ls.co.bytewise.api.student.StudentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class PromotionDto extends BaseDto {

    private Long studentId;
    private String admissionNumber;
    private String studentFirstName;
    private String studentLastName;

    private Long fromClassId;
    private String fromClassName;

    private Long toClassId;
    private String toClassName;

    private StudentStatus previousStatus;
    private StudentStatus newStatus;

    private String reason;
    private Instant promotedAt;
    private String promotedByName;
    private String batchId;

    public PromotionDto(Promotion p) {
        super(p);
        this.studentId = p.getStudent().getId();
        this.admissionNumber = p.getStudent().getAdmissionNumber();
        this.studentFirstName = p.getStudent().getUser().getFirstName();
        this.studentLastName = p.getStudent().getUser().getLastName();

        if (p.getFromClass() != null) {
            this.fromClassId = p.getFromClass().getId();
            this.fromClassName = p.getFromClass().getName();
        }
        if (p.getToClass() != null) {
            this.toClassId = p.getToClass().getId();
            this.toClassName = p.getToClass().getName();
        }

        this.previousStatus = p.getPreviousStatus();
        this.newStatus = p.getNewStatus();
        this.reason = p.getReason();
        this.promotedAt = p.getPromotedAt();
        this.batchId = p.getBatchId();

        if (p.getPromotedBy() != null) {
            this.promotedByName = p.getPromotedBy().getFullName();
        }
    }
}