package ls.co.bytewise.api.result;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import ls.co.bytewise.api.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "result_audit")
public class ResultAudit extends SchoolScopedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_result_id", nullable = false)
    private TermResult termResult;

    @Column(name = "previous_mark")
    private Double previousMark;

    @Column(name = "new_mark")
    private Double newMark;

    @Column(length = 1000)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;
}