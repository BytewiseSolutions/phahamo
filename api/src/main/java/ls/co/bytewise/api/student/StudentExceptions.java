package ls.co.bytewise.api.student;

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
}