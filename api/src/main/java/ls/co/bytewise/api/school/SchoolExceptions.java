package ls.co.bytewise.api.school;

public final class SchoolExceptions {

    private SchoolExceptions() {}

    public static class SchoolNotFoundException extends RuntimeException {
        public SchoolNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidTimezoneException extends RuntimeException {
        public InvalidTimezoneException(String message) {
            super(message);
        }
    }

    public static class InvalidSchoolProfileException extends RuntimeException {
        public InvalidSchoolProfileException(String message) {
            super(message);
        }
    }
}