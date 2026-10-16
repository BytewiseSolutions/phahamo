package ls.co.bytewise.api.assessment;

public class AssessmentExceptions {

    public static class AssessmentNotFoundException extends RuntimeException {
        public AssessmentNotFoundException(String message) { super(message); }
    }

    public static class InvalidAssessmentException extends RuntimeException {
        public InvalidAssessmentException(String message) { super(message); }
    }
}