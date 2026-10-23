package ls.co.bytewise.api.imports;

public class ImportExceptions {

    public static class ImportValidationException extends RuntimeException {
        public ImportValidationException(String message) { super(message); }
    }

    public static class InvalidFileException extends RuntimeException {
        public InvalidFileException(String message) { super(message); }
    }
}