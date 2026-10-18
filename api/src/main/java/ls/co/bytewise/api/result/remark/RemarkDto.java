package ls.co.bytewise.api.result.remark;

import ls.co.bytewise.api.common.BaseDto;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class RemarkDto extends BaseDto {
    private Long studentId;
    private String studentFirstName;
    private String studentLastName;
    private String admissionNumber;

    private String classTeacherRemark;
    private String classTeacherRemarkByName;
    private Instant classTeacherRemarkAt;

    private String principalRemark;
    private String principalRemarkByName;
    private Instant principalRemarkAt;

    private boolean canEditClassTeacherRemark;
    private boolean locked;

    public RemarkDto(Remark r, boolean locked, boolean canEditClassTeacherRemark) {
        super(r);
        this.studentId = r.getStudent().getId();
        this.studentFirstName = r.getStudent().getUser().getFirstName();
        this.studentLastName = r.getStudent().getUser().getLastName();
        this.admissionNumber = r.getStudent().getAdmissionNumber();

        this.classTeacherRemark = r.getClassTeacherRemark();
        if (r.getClassTeacherRemarkBy() != null) {
            this.classTeacherRemarkByName = r.getClassTeacherRemarkBy().getFullName();
        }
        this.classTeacherRemarkAt = r.getClassTeacherRemarkAt();

        this.principalRemark = r.getPrincipalRemark();
        if (r.getPrincipalRemarkBy() != null) {
            this.principalRemarkByName = r.getPrincipalRemarkBy().getFullName();
        }
        this.principalRemarkAt = r.getPrincipalRemarkAt();

        this.locked = locked;
        this.canEditClassTeacherRemark = canEditClassTeacherRemark;
    }
}