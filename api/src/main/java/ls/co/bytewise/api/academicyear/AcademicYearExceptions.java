package ls.co.bytewise.api.academicyear;

public final class AcademicYearExceptions {

    private AcademicYearExceptions() {}

    public static class AcademicYearNotFoundException extends RuntimeException {
        public AcademicYearNotFoundException(String message) {
            super(message);
        }
    }
}