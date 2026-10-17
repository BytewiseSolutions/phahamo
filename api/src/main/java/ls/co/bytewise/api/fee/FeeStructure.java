package ls.co.bytewise.api.fee;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.term.Term;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "fee_structure",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_fee_structure_class_term",
                columnNames = {"school_class_id", "term_id"}
        )
)
public class FeeStructure extends SchoolScopedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_class_id", nullable = false)
    private SchoolClass schoolClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @OneToMany(mappedBy = "feeStructure", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeeItem> items = new ArrayList<>();
}