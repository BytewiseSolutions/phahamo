package ls.co.bytewise.api.dashboard;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class BirthdayDto {

    private Long personId;
    private String name;
    private String type;
    private String className;
    private LocalDate date;
    private boolean today;

    public BirthdayDto(Long personId, String name, String type, String className,
                       LocalDate date, boolean today) {
        this.personId = personId;
        this.name = name;
        this.type = type;
        this.className = className;
        this.date = date;
        this.today = today;
    }
}
