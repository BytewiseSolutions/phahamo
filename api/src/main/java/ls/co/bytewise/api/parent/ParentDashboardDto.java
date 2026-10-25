package ls.co.bytewise.api.parent;

import ls.co.bytewise.api.announcement.AnnouncementDto;
import lombok.Getter;

import java.util.List;

@Getter
public class ParentDashboardDto {
    private final List<ParentDashboardChildDto> children;
    private final List<AnnouncementDto> announcements;

    public ParentDashboardDto(List<ParentDashboardChildDto> children,
                              List<AnnouncementDto> announcements) {
        this.children = children;
        this.announcements = announcements;
    }
}
