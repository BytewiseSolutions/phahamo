package ls.co.bytewise.api.grading;

public class GradeBandExceptions {

    public static class GradeBandNotFoundException extends RuntimeException {
        public GradeBandNotFoundException(String message) { super(message); }
    }

    public static class InvalidGradeBandException extends RuntimeException {
        public InvalidGradeBandException(String message) { super(message); }
    }

    public static class DuplicateGradeException extends RuntimeException {
        public DuplicateGradeException(String message) { super(message); }
    }
}