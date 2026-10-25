package ls.co.bytewise.api.student;

import lombok.Getter;

import java.util.List;

@Getter
public class DuplicateStudentException extends RuntimeException {
    private final List<StudentDuplicateDto> matches;

    public DuplicateStudentException(List<StudentDuplicateDto> matches) {
        super("A student with the same name and date of birth already exists in this school");
        this.matches = matches;
    }
}
