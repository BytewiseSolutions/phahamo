package ls.co.bytewise.api.schoolclass;

public final class SchoolClassExceptions {

    private SchoolClassExceptions() {}

    public static class SchoolClassNotFoundException extends RuntimeException {
        public SchoolClassNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidSchoolClassException extends RuntimeException {
        public InvalidSchoolClassException(String message) {
            super(message);
        }
    }
}