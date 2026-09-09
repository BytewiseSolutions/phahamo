package ls.co.bytewise.api.academicyear;

public final class AcademicYearExceptions {

    private AcademicYearExceptions() {}

    public static class AcademicYearNotFoundException extends RuntimeException {
        public AcademicYearNotFoundException(String message) {
            super(message);
        }
    }

    public static class LinkedDataExistsException extends RuntimeException {
        public LinkedDataExistsException(String message) {
            super(message);
        }
    }

    public static class InvalidAcademicYearStateException extends RuntimeException {
        public InvalidAcademicYearStateException(String message) {
            super(message);
        }
    }
}