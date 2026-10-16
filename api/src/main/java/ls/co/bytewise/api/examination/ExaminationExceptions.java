package ls.co.bytewise.api.examination;

public class ExaminationExceptions {

    public static class ExaminationNotFoundException extends RuntimeException {
        public ExaminationNotFoundException(String message) { super(message); }
    }

    public static class InvalidExaminationException extends RuntimeException {
        public InvalidExaminationException(String message) { super(message); }
    }

    public static class OverlappingExaminationException extends RuntimeException {
        public OverlappingExaminationException(String message) { super(message); }
    }
}