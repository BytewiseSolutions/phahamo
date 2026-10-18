package ls.co.bytewise.api.announcement;

import ls.co.bytewise.api.common.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class AnnouncementPayloadDto {
    private String title;
    private String body;
    private AnnouncementStatus status;
    private AudienceType audienceType;

    private Long targetClassId;
    private UserRole targetRole;
    private Long targetUserId;

    private Instant publishAt;
    private Instant expiresAt;
}