package ls.co.bytewise.api.term;

public final class TermExceptions {

    private TermExceptions() {}

    public static class TermNotFoundException extends RuntimeException {
        public TermNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidTermException extends RuntimeException {
        public InvalidTermException(String message) {
            super(message);
        }
    }
}