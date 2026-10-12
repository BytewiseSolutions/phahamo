package ls.co.bytewise.api.result;

public class TermResultExceptions {

    public static class TermResultNotFoundException extends RuntimeException {
        public TermResultNotFoundException(String message) { super(message); }
    }

    public static class InvalidTermResultOperationException extends RuntimeException {
        public InvalidTermResultOperationException(String message) { super(message); }
    }
}