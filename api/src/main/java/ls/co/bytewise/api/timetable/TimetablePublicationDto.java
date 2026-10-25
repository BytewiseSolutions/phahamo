package ls.co.bytewise.api.timetable;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class TimetablePublicationDto {
    private boolean published;
    private Instant publishedAt;
    private List<String> problems;
}
