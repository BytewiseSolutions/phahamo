package ls.co.bytewise.api.announcement;

import ls.co.bytewise.api.common.BaseDto;
import ls.co.bytewise.api.common.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class AnnouncementDto extends BaseDto {

    private String title;
    private String body;
    private AnnouncementStatus status;
    private AudienceType audienceType;

    private Long targetClassId;
    private String targetClassName;

    private UserRole targetRole;

    private Long targetUserId;
    private String targetUserName;

    private Instant publishAt;
    private Instant expiresAt;

    private Long authorId;
    private String authorName;

    public AnnouncementDto(Announcement a) {
        super(a);
        this.title = a.getTitle();
        this.body = a.getBody();
        this.status = a.getStatus();
        this.audienceType = a.getAudienceType();

        if (a.getTargetClass() != null) {
            this.targetClassId = a.getTargetClass().getId();
            this.targetClassName = a.getTargetClass().getName();
        }
        this.targetRole = a.getTargetRole();

        if (a.getTargetUser() != null) {
            this.targetUserId = a.getTargetUser().getId();
            this.targetUserName = a.getTargetUser().getFullName();
        }

        this.publishAt = a.getPublishAt();
        this.expiresAt = a.getExpiresAt();
        this.authorId = a.getAuthor().getId();
        this.authorName = a.getAuthor().getFullName();
    }
}