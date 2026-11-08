package ls.co.bytewise.api.dataexport;

import jakarta.persistence.*;
import ls.co.bytewise.api.common.DataFile;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.user.User;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter @Setter @Entity @Table(name = "school_data_export")
public class SchoolDataExport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 80)
    private String token;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "school_id")
    private School school;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "requested_by_id")
    private User requestedBy;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private SchoolDataExportStatus status;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "file_id")
    private DataFile file;
    @Column(nullable = false) private Instant expiresAt;
    @Column(nullable = false) private Instant createdAt;
    private Instant completedAt;
}
