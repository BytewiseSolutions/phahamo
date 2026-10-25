package ls.co.bytewise.api.student;

import lombok.Getter;

import java.util.List;

public final class StudentExceptions {

    private StudentExceptions() {}

    public static class StudentNotFoundException extends RuntimeException {
        public StudentNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidStudentOperationException extends RuntimeException {
        public InvalidStudentOperationException(String message) {
            super(message);
        }
    }

    @Getter
    public static class FeeChangeConfirmationRequiredException extends RuntimeException {
        private final StudentClassFeePreviewDto preview;

        public FeeChangeConfirmationRequiredException(StudentClassFeePreviewDto preview) {
            super("The student's fees will change because of this class move. Confirmation is required.");
            this.preview = preview;
        }
    }

    @Getter
    public static class DuplicateStudentException extends RuntimeException {
        private final List<StudentDuplicateDto> matches;

        public DuplicateStudentException(List<StudentDuplicateDto> matches) {
            super("A student with the same name and date of birth already exists in this school");
            this.matches = matches;
        }
    }
}
