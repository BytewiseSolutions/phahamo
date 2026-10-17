package ls.co.bytewise.api.announcement;

import jakarta.persistence.*;
import ls.co.bytewise.api.common.SchoolScopedEntity;
import ls.co.bytewise.api.schoolclass.SchoolClass;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.common.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "announcement")
public class Announcement extends SchoolScopedEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnouncementStatus status = AnnouncementStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AudienceType audienceType = AudienceType.ALL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_class_id")
    private SchoolClass targetClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_role")
    private UserRole targetRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    @Column(nullable = false)
    private Instant publishAt;

    private Instant expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}