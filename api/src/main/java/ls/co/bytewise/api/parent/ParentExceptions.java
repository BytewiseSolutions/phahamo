package ls.co.bytewise.api.parent;

public final class ParentExceptions {

    private ParentExceptions() {}

    public static class ParentNotFoundException extends RuntimeException {
        public ParentNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateLinkException extends RuntimeException {
        public DuplicateLinkException(String message) {
            super(message);
        }
    }

    public static class LinkNotFoundException extends RuntimeException {
        public LinkNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidSearchException extends RuntimeException {
        public InvalidSearchException(String message) {
            super(message);
        }
    }
}