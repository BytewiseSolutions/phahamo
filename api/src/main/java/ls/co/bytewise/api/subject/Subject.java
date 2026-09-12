package ls.co.bytewise.api.subject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "subject")
public class Subject extends SchoolScopedEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;
}