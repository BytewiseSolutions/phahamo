package ls.co.bytewise.api.school;

import jakarta.persistence.*;
import ls.co.bytewise.api.common.BaseEntity;
import ls.co.bytewise.api.common.DataFile;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TermStructure termStructure = TermStructure.THREE_TERM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency = Currency.LSL;

    @Column(nullable = false)
    private String timezone = "Africa/Maseru";

    @Column(name = "absence_alerts_enabled", nullable = false)
    private boolean absenceAlertsEnabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logo_file_id")
    private DataFile logo;
}