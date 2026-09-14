package ls.co.bytewise.api.parent;

public final class ParentExceptions {

    private ParentExceptions() {}

    public static class ParentNotFoundException extends RuntimeException {
        public ParentNotFoundException(String message) {
            super(message);
        }
    }
}