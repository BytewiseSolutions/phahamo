package ls.co.bytewise.api.school;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import ls.co.bytewise.api.common.BaseEntity;
import ls.co.bytewise.api.common.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "school")
public class School extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, unique = true)
    private String contactEmail;

    @Column(nullable = false)
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;
}