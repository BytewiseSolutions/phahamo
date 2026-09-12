package ls.co.bytewise.api.subject;

public final class SubjectExceptions {

    private SubjectExceptions() {}

    public static class SubjectNotFoundException extends RuntimeException {
        public SubjectNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateSubjectCodeException extends RuntimeException {
        public DuplicateSubjectCodeException(String message) {
            super(message);
        }
    }
    public static class LinkedDataExistsException extends RuntimeException {
        public LinkedDataExistsException(String message) {
            super(message);
        }
    }
}